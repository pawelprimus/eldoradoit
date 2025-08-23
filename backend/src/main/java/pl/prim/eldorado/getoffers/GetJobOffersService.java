package pl.prim.eldorado.getoffers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.prim.eldorado.getoffers.dto.CityTechnologyOfferDto;
import pl.prim.eldorado.getoffers.dto.OfferCountDto;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.JobOfferStatistics;
import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.enums.Technology;
import pl.prim.eldorado.fetchoffers.JobOfferStatisticsRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
class GetJobOffersService {

    private final JobOfferStatisticsRepository repository;

    @Autowired
    GetJobOffersService(JobOfferStatisticsRepository repository) {
        this.repository = repository;
    }

    public List<CityTechnologyOfferDto> getFilteredOffers(City city, Technology technology) {
        // Input validation
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(technology, "Technology cannot be null");

        List<JobOfferStatistics> statistics = repository.findByCityAndTechnology(city, technology);

        return statistics.stream()
                .map(CityTechnologyOfferDto::from)
                .sorted(this::compareOffers)
                .toList();
    }

    private int compareOffers(CityTechnologyOfferDto a, CityTechnologyOfferDto b) {
        int dateCompare = b.fetchDate().compareTo(a.fetchDate());
        if (dateCompare != 0) return dateCompare;

        int cityCompare = a.city().compareTo(b.city());
        if (cityCompare != 0) return cityCompare;

        return a.technology().compareTo(b.technology());
    }
}

