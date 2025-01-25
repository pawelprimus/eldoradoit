package pl.prim.eldorado.scrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class Scheduler {

    //private final OfferCountService offerCountService;
    private final JobOfferStatisticsService jobOfferStatisticsService;

    // Schedules the every second
//    @Scheduled(cron = "0 * * * * *")
//    public void runDailyJobOfferStatisticJob() {
//        log.info("Start scheduling.");
//        jobOfferStatisticsService.collectStatisticsForAllCombinations()
//                .doOnSuccess(unused -> log.info("Daily offer count successfully saved."))
//                .doOnError(error -> log.error("Failed to save daily offer count: {}", error.getMessage()))
//                .subscribe();
//    }

//    // Schedules the every second
//    @Scheduled(cron = "0 * * * * *")
//    public void runDailyOfferCountJob() {
//        log.info("Start scheduling.");
////        offerCountService.fetchAndSaveOfferCount()
////                .doOnSuccess(unused -> log.info("Daily offer count successfully saved."))
////                .doOnError(error -> log.error("Failed to save daily offer count: {}", error.getMessage()))
////                .subscribe();
//    }

}
