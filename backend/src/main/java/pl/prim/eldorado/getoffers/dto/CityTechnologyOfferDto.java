package pl.prim.eldorado.getoffers.dto;

import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.JobOfferStatistics;
import pl.prim.eldorado.model.stats.enums.Technology;

import java.time.LocalDate;
import java.util.Map;


public record CityTechnologyOfferDto(LocalDate fetchDate, City city, Technology technology,
                                     Map<ExperienceLevel, Integer> offerCounts) {

    public static CityTechnologyOfferDto from(JobOfferStatistics statistics) {
        return new CityTechnologyOfferDto(
                statistics.getFetchDate().toLocalDate(),
                statistics.getCity(),
                statistics.getTechnology(),
                statistics.getOfferCounts()
        );
    }
}