package pl.prim.eldorado.scrap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.prim.eldorado.model.City;
import pl.prim.eldorado.model.JobOfferStatistics;
import pl.prim.eldorado.model.Technology;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobOfferStatisticsRepository extends JpaRepository<JobOfferStatistics, Long> {
    List<JobOfferStatistics> findByFetchDateBetween(LocalDateTime start, LocalDateTime end);

    Optional<JobOfferStatistics> findByCityAndTechnologyAndFetchDate(City city, Technology technology, LocalDateTime fetchDate);
}