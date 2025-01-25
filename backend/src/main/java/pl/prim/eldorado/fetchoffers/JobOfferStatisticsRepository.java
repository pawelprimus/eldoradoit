package pl.prim.eldorado.fetchoffers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.JobOfferStatistics;
import pl.prim.eldorado.model.stats.enums.Technology;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobOfferStatisticsRepository extends JpaRepository<JobOfferStatistics, Long> {
    List<JobOfferStatistics> findByFetchDateBetween(LocalDateTime start, LocalDateTime end);

    Optional<JobOfferStatistics> findByCityAndTechnologyAndFetchDate(City city, Technology technology, LocalDateTime fetchDate);

    List<JobOfferStatistics> findByCityAndTechnology(City city, Technology technology);
}