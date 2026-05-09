package pl.prim.eldorado.fetchoffers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import pl.prim.eldorado.model.stats.OfferCountResponse;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.enums.Technology;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

@EnabledIfSystemProperty(named = "live", matches = "true")
class JobOfferStatisticsLiveIT {

    private static final String BASE_URL = "https://api.justjoin.it/v2/user-panel/offers";

    @Test
    void probeUrlBuilderAndUpstream() throws Exception {
        Method urlBuilder = JobOfferStatisticsService.class.getDeclaredMethod(
                "urlBuilder", City.class, Technology.class, ExperienceLevel.class);
        urlBuilder.setAccessible(true);

        WebClient client = WebClient.builder()
                .baseUrl(BASE_URL)
                .filter(logRequest())
                .build();

        List<ExperienceLevel> levels = List.of(
                ExperienceLevel.ALL,
                ExperienceLevel.JUNIOR,
                ExperienceLevel.MID,
                ExperienceLevel.SENIOR,
                ExperienceLevel.C_LEVEL);

        City city = City.ALL;
        Technology tech = Technology.ALL;

        System.out.println("=== Probing " + city + " / " + tech + " ===");
        for (ExperienceLevel level : levels) {
            String relative = (String) urlBuilder.invoke(null, city, tech, level);
            System.out.println("\nlevel=" + level);
            System.out.println("  urlBuilder() returned : " + relative);

            Integer count = client.get()
                    .uri(relative)
                    .retrieve()
                    .bodyToMono(OfferCountResponse.class)
                    .map(OfferCountResponse::count)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        System.out.println("  ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                        return Mono.empty();
                    })
                    .block();
            System.out.println("  count                 : " + count);
        }
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            System.out.println("  WebClient sent URL    : " + req.url());
            return Mono.just(ClientRequest.from(req).build());
        });
    }
}
