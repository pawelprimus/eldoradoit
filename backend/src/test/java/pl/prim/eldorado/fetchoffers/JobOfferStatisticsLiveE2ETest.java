package pl.prim.eldorado.fetchoffers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import pl.prim.eldorado.model.stats.JobOfferStatistics;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.enums.Technology;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end test that hits the real JustJoin.it API, runs the full fetch + save
 * pipeline against an in-memory H2 database, then reads the persisted rows back
 * and verifies the offerCounts map round-trips intact.
 *
 * Disabled by default. Enable with -Dlive=true (the run will take ~30s).
 */
@EnabledIfSystemProperty(named = "live", matches = "true")
@SpringBootTest
@ActiveProfiles("e2e")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:eldorado-e2e;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "app.base-url=https://api.justjoin.it/v2/user-panel/offers",
        "app.cors.allowed-origins=http://localhost:3000",
        "app.admin.secret-key=test-secret",
        "app.statistics.delay.requests=1",
        "app.statistics.delay.combinations=1",
        "app.statistics.concurrent.requests=1",
        "logging.level.pl.prim.eldorado=DEBUG"
})
class JobOfferStatisticsLiveE2ETest {

    @Autowired
    private JobOfferStatisticsService service;

    @Autowired
    private JobOfferStatisticsRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final List<Combination> COMBINATIONS = List.of(
            new Combination(City.ALL, Technology.ALL),
            new Combination(City.ALL, Technology.JAVA),
            new Combination(City.WARSAW, Technology.ALL),
            new Combination(City.WARSAW, Technology.JAVA)
    );

    @Test
    void fullPipelineRoundTrip() throws Exception {
        ReflectionTestUtils.setField(service, "delayBetweenRequestsSeconds", 1);
        ReflectionTestUtils.setField(service, "delayBetweenCombinationsSeconds", 0);

        Method processCombo = JobOfferStatisticsService.class.getDeclaredMethod(
                "processExperienceLevelsForCityAndTechnology",
                City.class, Technology.class, LocalDateTime.class);
        processCombo.setAccessible(true);

        Method saveStats = JobOfferStatisticsService.class.getDeclaredMethod(
                "saveStatistics", JobOfferStatistics.class);
        saveStats.setAccessible(true);

        LocalDateTime fetchTime = LocalDateTime.now();

        for (Combination combo : COMBINATIONS) {
            System.out.println();
            System.out.println("================================================================");
            System.out.println("[E2E] Combination: city=" + combo.city + ", technology=" + combo.technology);
            System.out.println("================================================================");

            @SuppressWarnings("unchecked")
            var fetchedMono =
                    (reactor.core.publisher.Mono<JobOfferStatistics>)
                            processCombo.invoke(service, combo.city, combo.technology, fetchTime);

            JobOfferStatistics fetched = fetchedMono.block(java.time.Duration.ofSeconds(60));

            assertThat(fetched)
                    .as("processExperienceLevelsForCityAndTechnology returned null for %s — incomplete data?", combo)
                    .isNotNull();

            System.out.println("[E2E] FETCHED — about to save:");
            logCounts(fetched.getOfferCounts());

            assertThat(fetched.getOfferCounts())
                    .as("expected one entry per ExperienceLevel after fetch for %s", combo)
                    .hasSize(ExperienceLevel.values().length);

            assertDistinctOrAtLeastNotConstant(fetched.getOfferCounts(), combo);

            @SuppressWarnings("unchecked")
            var savedMono =
                    (reactor.core.publisher.Mono<JobOfferStatistics>)
                            saveStats.invoke(service, fetched);
            JobOfferStatistics saved = savedMono.block(java.time.Duration.ofSeconds(30));

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).as("saved entity must have an id").isNotNull();

            Map<ExperienceLevel, Integer> reloadedCounts = transactionTemplate.execute(status -> {
                JobOfferStatistics reloaded = repository.findById(saved.getId()).orElseThrow();
                assertThat(reloaded.getCity()).isEqualTo(combo.city);
                assertThat(reloaded.getTechnology()).isEqualTo(combo.technology);
                return new java.util.EnumMap<>(reloaded.getOfferCounts());
            });

            System.out.println("[E2E] RELOADED FROM DB:");
            logCounts(reloadedCounts);

            assertThat(reloadedCounts)
                    .as("[BUG IF FAILS] reloaded offerCounts must equal what we saved for %s", combo)
                    .containsExactlyInAnyOrderEntriesOf(fetched.getOfferCounts());
        }

        System.out.println();
        System.out.println("[E2E] All " + COMBINATIONS.size() + " combinations round-tripped successfully.");
    }

    private static void logCounts(Map<ExperienceLevel, Integer> counts) {
        for (ExperienceLevel level : ExperienceLevel.values()) {
            System.out.printf("        %-8s = %s%n", level.name(), counts.get(level));
        }
    }

    private static void assertDistinctOrAtLeastNotConstant(
            Map<ExperienceLevel, Integer> counts, Combination combo) {
        long distinctValues = counts.values().stream().distinct().count();
        if (distinctValues == 1) {
            System.out.println(
                    "[E2E][WARN] All experience levels returned the same count for " + combo
                            + " — this matches the production bug symptom! "
                            + "Either the upstream API really returns equal numbers (unlikely for ALL vs juniors) "
                            + "or urlBuilder/WebClient is dropping the experienceLevels[] parameter.");
        }
    }

    private record Combination(City city, Technology technology) {
        @Override
        public String toString() {
            return city + "/" + technology;
        }
    }
}
