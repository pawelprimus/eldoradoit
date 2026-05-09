package pl.prim.eldorado.fetchoffers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import pl.prim.eldorado.model.stats.JobOfferStatistics;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.enums.Technology;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobOfferStatisticsServiceTest {

    @Mock
    private JobOfferStatisticsRepository jobOfferStatisticsRepository;

    @Mock
    private FailedOperationRepository failedOperationRepository;

    private RecordingExchangeFunction exchangeFunction;
    private JobOfferStatisticsService service;

    private static final Map<ExperienceLevel, Integer> COUNTS_PER_LEVEL = Map.of(
            ExperienceLevel.ALL, 1000,
            ExperienceLevel.JUNIOR, 100,
            ExperienceLevel.MID, 400,
            ExperienceLevel.SENIOR, 450,
            ExperienceLevel.C_LEVEL, 50
    );

    @BeforeEach
    void setUp() {
        exchangeFunction = new RecordingExchangeFunction(COUNTS_PER_LEVEL);
        WebClient webClient = WebClient.builder()
                .baseUrl("http://test.local")
                .exchangeFunction(exchangeFunction)
                .build();

        service = new JobOfferStatisticsService(
                jobOfferStatisticsRepository,
                failedOperationRepository,
                webClient
        );
        ReflectionTestUtils.setField(service, "delayBetweenRequestsSeconds", 0);
        ReflectionTestUtils.setField(service, "delayBetweenCombinationsSeconds", 0);
        ReflectionTestUtils.setField(service, "maxConcurrentRequests", 1);

        lenient().when(jobOfferStatisticsRepository.save(any(JobOfferStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void collectExperienceLevelData_returnsDistinctCountPerLevel() throws Exception {
        Method method = JobOfferStatisticsService.class.getDeclaredMethod(
                "collectExperienceLevelData", City.class, Technology.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Mono<Map<ExperienceLevel, Integer>> result =
                (Mono<Map<ExperienceLevel, Integer>>) method.invoke(service, City.WARSAW, Technology.JAVA);

        Map<ExperienceLevel, Integer> map = result.block();

        assertThat(map)
                .as("each experience level must map to its own count")
                .containsExactlyInAnyOrderEntriesOf(COUNTS_PER_LEVEL);

        assertThat(exchangeFunction.requestedUrls)
                .as("urlBuilder must build a distinct URL per experience level")
                .hasSize(ExperienceLevel.values().length);
    }

    @Test
    void processCombination_persistsStatisticsWithFullMapPerLevel() throws Exception {
        Method method = JobOfferStatisticsService.class.getDeclaredMethod(
                "processTechnologiesForCity", City.class, LocalDateTime.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        var flux = (reactor.core.publisher.Flux<JobOfferStatistics>)
                method.invoke(service, City.WARSAW, LocalDateTime.now());

        StepVerifier.create(flux)
                .expectNextCount(Technology.values().length)
                .verifyComplete();

        ArgumentCaptor<JobOfferStatistics> captor = ArgumentCaptor.forClass(JobOfferStatistics.class);
        verify(jobOfferStatisticsRepository, times(Technology.values().length)).save(captor.capture());

        for (JobOfferStatistics saved : captor.getAllValues()) {
            assertThat(saved.getCity()).isEqualTo(City.WARSAW);
            assertThat(saved.getOfferCounts())
                    .as("each saved row must contain ALL five distinct counts, not the same value repeated")
                    .containsExactlyInAnyOrderEntriesOf(COUNTS_PER_LEVEL);
        }
    }

    @Test
    void fetchOfferCount_buildsUrlWithCorrectExperienceLevel() throws Exception {
        Method fetch = JobOfferStatisticsService.class.getDeclaredMethod(
                "fetchOfferCount", City.class, Technology.class, ExperienceLevel.class);
        fetch.setAccessible(true);

        for (ExperienceLevel level : ExperienceLevel.values()) {
            exchangeFunction.requestedUrls.clear();
            @SuppressWarnings("unchecked")
            Mono<Integer> mono = (Mono<Integer>) fetch.invoke(service, City.WARSAW, Technology.JAVA, level);
            Integer count = mono.block();

            assertThat(count)
                    .as("count for level %s must match what the URL-keyed stub returns", level)
                    .isEqualTo(COUNTS_PER_LEVEL.get(level));

            String requestedUrl = exchangeFunction.requestedUrls.get(0);
            if (level == ExperienceLevel.ALL) {
                assertThat(requestedUrl)
                        .as("ALL means no experienceLevels[] param")
                        .doesNotContain("experienceLevels[]");
            } else {
                assertThat(requestedUrl)
                        .contains("experienceLevels[]=" + level.getDisplayName());
            }
        }
    }

    /**
     * ExchangeFunction stub that returns a count keyed by the ExperienceLevel parameter
     * present (or absent) in the request URL. Records every URL it sees so the test
     * can assert that urlBuilder produced distinct URLs per level.
     */
    private static final class RecordingExchangeFunction implements ExchangeFunction {

        final List<String> requestedUrls = new java.util.concurrent.CopyOnWriteArrayList<>();
        private final Map<ExperienceLevel, Integer> countsPerLevel;

        RecordingExchangeFunction(Map<ExperienceLevel, Integer> countsPerLevel) {
            this.countsPerLevel = new EnumMap<>(countsPerLevel);
        }

        @Override
        public Mono<ClientResponse> exchange(org.springframework.web.reactive.function.client.ClientRequest request) {
            String rawUrl = request.url().toString();
            String url = java.net.URLDecoder.decode(rawUrl, java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("[STUB] WebClient sent URL: " + url);
            requestedUrls.add(url);

            ExperienceLevel level = inferLevelFromUrl(url);
            Integer count = countsPerLevel.get(level);

            String body = "{\"count\":" + count + "}";
            ClientResponse response = ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .build();
            return Mono.just(response);
        }

        private ExperienceLevel inferLevelFromUrl(String url) {
            for (ExperienceLevel level : ExperienceLevel.values()) {
                if (level == ExperienceLevel.ALL) {
                    continue;
                }
                if (url.contains("experienceLevels[]=" + level.getDisplayName())) {
                    return level;
                }
            }
            return ExperienceLevel.ALL;
        }
    }
}
