//package pl.prim.eldorado.initialization;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import pl.prim.eldorado.model.City;
//import pl.prim.eldorado.model.ExperienceLevel;
//import pl.prim.eldorado.model.JobOfferStatistics;
//import pl.prim.eldorado.model.Technology;
//import pl.prim.eldorado.fetchoffers.JobOfferStatisticsRepository;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer {
//
//    private final JobOfferStatisticsRepository repository;
//
//    @Bean
//    public ApplicationRunner initializer() {
//        return args -> {
//            List<JobOfferStatistics> offerCountList = new ArrayList<>();
//            Random random = new Random();
//
//            for (int i = 100; i > 0; i--) {
//                LocalDateTime date = LocalDateTime.now().minusDays(i);
//                Map<ExperienceLevel, Integer> map = new HashMap<>();
//                map.put(ExperienceLevel.ALL, 80 + random.nextInt(50));
//                map.put(ExperienceLevel.JUNIOR, 0 + random.nextInt(50));
//                map.put(ExperienceLevel.MID, 10 + random.nextInt(50));
//                map.put(ExperienceLevel.SENIOR, 20 + random.nextInt(50));
//                map.put(ExperienceLevel.C_LEVEL, 25 + random.nextInt(50));
//
//                offerCountList.add(JobOfferStatistics
//                        .builder()
//                        .fetchDate(date)
//                        .city(City.ALL)
//                        .technology(Technology.ALL)
//                        .offerCounts(map)
//                        .build());
//
//            }
//            repository.saveAll(offerCountList);
//            log.info("100 random initial data entries saved to repository!");
//        };
//    }
//}
