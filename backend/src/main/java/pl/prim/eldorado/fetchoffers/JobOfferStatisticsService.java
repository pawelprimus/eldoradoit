package pl.prim.eldorado.fetchoffers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.enums.Technology;
import pl.prim.eldorado.model.fails.FailedOperation;
import pl.prim.eldorado.model.fails.FailureMetadata;
import pl.prim.eldorado.model.stats.JobOfferStatistics;
import pl.prim.eldorado.model.stats.OfferCountResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferStatisticsService {

    private final JobOfferStatisticsRepository jobOfferStatisticsRepository;
    private final FailedOperationRepository failedOperationRepository;
    private final WebClient webClient;

    private static final int RETRY_ATTEMPTS = 3;
    private static final Duration INITIAL_RETRY_DELAY = Duration.ofSeconds(1);
    private static final Duration MAX_RETRY_DELAY = Duration.ofSeconds(5);

    // Configuration properties should be externalized
    @Value("${app.statistics.delay.requests:2}")
    private int delayBetweenRequestsSeconds;

    @Value("${app.statistics.delay.combinations:5}")
    private int delayBetweenCombinationsSeconds;

    @Value("${app.statistics.concurrent.requests:1}")
    private int maxConcurrentRequests;


    public Mono<Void> collectStatisticsForAllCombinations() {
        LocalDateTime fetchTime = LocalDateTime.now();
        return processCities(fetchTime).then();
    }

    private Flux<JobOfferStatistics> processCities(LocalDateTime fetchTime) {
        return Flux.fromArray(City.values())
                .delayElements(Duration.ofSeconds(delayBetweenCombinationsSeconds))
                .flatMap(city -> processTechnologiesForCity(city, fetchTime), maxConcurrentRequests);
    }

    private Flux<JobOfferStatistics> processTechnologiesForCity(City city, LocalDateTime fetchTime) {
        return Flux.fromArray(Technology.values())
                .delayElements(Duration.ofSeconds(delayBetweenCombinationsSeconds))
                .flatMap(technology -> processExperienceLevelsForCityAndTechnology(city, technology, fetchTime),
                        maxConcurrentRequests)
                .flatMap(this::saveStatistics)
                .retryWhen(createRetrySpec())
                .onErrorContinue(this::handleError);
    }

    private Mono<JobOfferStatistics> processExperienceLevelsForCityAndTechnology(
            City city, Technology technology, LocalDateTime fetchTime) {
        log.info("Starting collection for City: {}, Technology: {}",
                city.getDisplayName(), technology.getDisplayName());

        return collectExperienceLevelData(city, technology)
                .map(experienceLevelMap -> createStatistics(city, technology, fetchTime, experienceLevelMap))
                .filter(stats -> stats != null);
    }

    private Mono<Map<ExperienceLevel, Integer>> collectExperienceLevelData(City city, Technology technology) {
        return Flux.fromArray(ExperienceLevel.values())
                .delayElements(Duration.ofSeconds(delayBetweenRequestsSeconds))
                .flatMap(experienceLevel ->
                                fetchOfferCount(city, technology, experienceLevel)
                                        .map(count -> Map.entry(experienceLevel, count)),
                        maxConcurrentRequests)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private JobOfferStatistics createStatistics(
            City city, Technology technology, LocalDateTime fetchTime,
            Map<ExperienceLevel, Integer> experienceLevelMap) {
        if (experienceLevelMap.size() == ExperienceLevel.values().length) {
            log.info("Collected all experience levels for City: {}, Technology: {}",
                    city.getDisplayName(), technology.getDisplayName());
            return JobOfferStatistics.builder()
                    .fetchDate(fetchTime)
                    .city(city)
                    .technology(technology)
                    .offerCounts(experienceLevelMap)
                    .build();
        } else {
            logIncompleteData(city, technology, experienceLevelMap.size());
            return null;
        }
    }

    private Mono<JobOfferStatistics> saveStatistics(JobOfferStatistics statistics) {
        return Mono.fromCallable(() -> {
                    // Validate input before saving
                    if (statistics == null) {
                        throw new IllegalArgumentException("Statistics cannot be null");
                    }
                    if (statistics.getOfferCounts() == null || statistics.getOfferCounts().isEmpty()) {
                        throw new IllegalArgumentException("Offer counts cannot be null or empty");
                    }
                    return jobOfferStatisticsRepository.save(statistics);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(30)) // Add timeout to prevent hanging
                .doOnSuccess(saved -> logSuccessfulSave(statistics))
                .doOnError(error -> logSaveError(statistics, error))
                .onErrorResume(Exception.class, error -> {
                    // Specific error handling based on type
                    if (error instanceof IllegalArgumentException) {
                        log.error("Invalid statistics data: {}", error.getMessage());
                    } else if (error instanceof TimeoutException) {
                        log.error("Database operation timed out for statistics: city={}, technology={}",
                                statistics.getCity(), statistics.getTechnology());
                    } else if (error instanceof DataIntegrityViolationException) {
                        log.error("Data integrity violation while saving statistics: {}", error.getMessage());
                    }

                    // Store failed operation in separate error log table
                    return storeFailedOperation(statistics, error)
                            .then(Mono.error(error)); // Propagate the error after logging
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof IllegalArgumentException)) // Don't retry validation errors
                        .maxBackoff(Duration.ofSeconds(5))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            log.error("Failed to save statistics after {} attempts", retrySignal.totalRetries());
                            return retrySignal.failure();
                        }));
    }

    private Mono<Void> storeFailedOperation(JobOfferStatistics statistics, Throwable error) {
        FailedOperation failedOpWithMetadata = FailedOperation.builder()
                .timestamp(LocalDateTime.now())
                .operation("SAVE_STATISTICS")
                .city(statistics.getCity().getDisplayName())
                .technology(statistics.getTechnology().getDisplayName())
                .metadata(FailureMetadata.builder()
                        .applicationVersion("1.0.0")
                        .environment("prod")
                        .host("server-1")
                        .build())
                .build();

        return Mono.fromCallable(() -> failedOperationRepository.save(failedOpWithMetadata))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    log.error("Failed to save error log: {}", e.getMessage());
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Integer> fetchOfferCount(City city, Technology technology, ExperienceLevel experienceLevel) {
        String url = urlBuilder(city, technology, experienceLevel);
        log.info("Fetching offer count for URL: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OfferCountResponse.class)
                .map(OfferCountResponse::count)
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> logFetchError(url, error))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(3)));
    }

    private static String urlBuilder(City city, Technology technology, ExperienceLevel experienceLevel) {
        StringBuilder baseUrl = new StringBuilder("/count?");
        if (city != City.ALL) {
            baseUrl.append("&city=").append(city.getDisplayName());
        }
        if (technology != Technology.ALL) {
            baseUrl.append("&categories[]=").append(technology.getDisplayName());
        }
        if (experienceLevel != ExperienceLevel.ALL) {
            baseUrl.append("&experienceLevels[]=").append(experienceLevel.getDisplayName());
        }
        return baseUrl.toString();
    }

    private static Retry createRetrySpec() {
        return Retry.backoff(RETRY_ATTEMPTS, INITIAL_RETRY_DELAY)
                .maxBackoff(MAX_RETRY_DELAY)
                .doAfterRetry(signal ->
                        log.warn("Retrying after error: {}", signal.failure().getMessage()));
    }

    // Logging methods
    private void logIncompleteData(City city, Technology technology, int actualSize) {
        log.warn("Incomplete data for City: {}, Technology: {}. Expected {} experience levels, got {}",
                city.getDisplayName(),
                technology.getDisplayName(),
                ExperienceLevel.values().length,
                actualSize);
    }

    private void logSuccessfulSave(JobOfferStatistics statistics) {
        log.info("Saved statistics for City: {}, Technology: {}, Experience Levels: {}",
                statistics.getCity().getDisplayName(),
                statistics.getTechnology().getDisplayName(),
                statistics.getOfferCounts().keySet());
    }

    private void logSaveError(JobOfferStatistics statistics, Throwable error) {
        log.error("Error saving statistics for City: {}, Technology: {}: {}",
                statistics.getCity().getDisplayName(),
                statistics.getTechnology().getDisplayName(),
                error.getMessage());
    }

    private void logFetchError(String url, Throwable error) {
        log.error("Error fetching offer count for URL {}: {}", url, error.getMessage());
    }

    private void handleError(Throwable error, Object obj) {
        log.error("Error processing item: {}, continuing with next", error.getMessage());
    }
}