package pl.prim.eldorado.getoffers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.prim.eldorado.model.ExperienceLevel;
import pl.prim.eldorado.model.JobOfferStatistics;
import pl.prim.eldorado.model.OfferCountDto;
import pl.prim.eldorado.scrap.JobOfferStatisticsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
class GetJobOffersService {

    private final JobOfferStatisticsRepository repository;

    @Autowired
    GetJobOffersService(JobOfferStatisticsRepository repository) {
        this.repository = repository;
    }

    List<OfferCountDto> getAll() {
        for (JobOfferStatistics jos : repository.findAll()) {
            log.info(jos.toString());
        }
        return repository.findAll().stream()
                .map(offer -> new OfferCountDto(offer.getOfferCounts().get(ExperienceLevel.ALL), offer.getFetchDate().toLocalDate()))
                .collect(Collectors.toList());
    }
}

