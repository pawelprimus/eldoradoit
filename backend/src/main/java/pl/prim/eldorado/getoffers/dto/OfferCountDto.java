package pl.prim.eldorado.getoffers.dto;

import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.JobOfferStatistics;

import java.time.LocalDate;

public record OfferCountDto(Integer count, LocalDate date) {

    public static OfferCountDto from(JobOfferStatistics statistics) {
        return new OfferCountDto(
                statistics.getOfferCounts().get(ExperienceLevel.ALL),
                statistics.getFetchDate().toLocalDate()
        );
    }
}