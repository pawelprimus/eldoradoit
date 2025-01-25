package pl.prim.eldorado.getoffers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.prim.eldorado.getoffers.dto.CityTechnologyOfferDto;
import pl.prim.eldorado.getoffers.dto.OfferCountDto;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.JobOfferStatistics;
import pl.prim.eldorado.model.stats.enums.Technology;
import pl.prim.eldorado.fetchoffers.JobOfferStatisticsRepository;

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
        if(log.isDebugEnabled()){
            for (JobOfferStatistics jos : repository.findAll()) {
                log.debug(jos.toString());
            }
        }

        return repository.findAll().stream()
                .map(OfferCountDto::from)
                .collect(Collectors.toList());
    }

    List<CityTechnologyOfferDto> getOffersWithLevels() {
        List<CityTechnologyOfferDto> cityTechnologyOfferDtos = repository
                .findByCityAndTechnology(City.ALL, Technology.ALL)
                .stream()
                .map(CityTechnologyOfferDto::from)
                .toList();

        return cityTechnologyOfferDtos;
    }
}

