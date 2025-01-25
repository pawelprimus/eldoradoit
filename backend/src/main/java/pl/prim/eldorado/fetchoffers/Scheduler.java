package pl.prim.eldorado.fetchoffers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.prim.eldorado.trigger.TriggerService;
import pl.prim.eldorado.trigger.TriggerType;

import static pl.prim.eldorado.trigger.TriggerType.SCHEDULED;

@Component
@RequiredArgsConstructor
@Slf4j
class Scheduler {

    private final JobOfferStatisticsService jobOfferStatisticsService;
    private final TriggerService triggerService;

    // at 5:00 AM every day
    @Scheduled(cron = "0 0 5 * * *")
    public void runDailyJobOfferStatisticJob() {
        log.info("Start scheduling.");
        triggerService.fetchWasTriggered(SCHEDULED);
        jobOfferStatisticsService.collectStatisticsForAllCombinations()
                .doOnSuccess(unused -> log.info("Daily offer count successfully saved."))
                .doOnError(error -> log.error("Failed to save daily offer count: {}", error.getMessage()))
                .subscribe();
    }

}
