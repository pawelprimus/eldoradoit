package pl.prim.eldorado.getoffers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.prim.eldorado.getoffers.dto.CityTechnologyOfferDto;
import pl.prim.eldorado.getoffers.dto.OfferCountDto;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.enums.Technology;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobOffersController {

    private final GetJobOffersService getJobOffersService;

    JobOffersController(GetJobOffersService getJobOffersService) {
        this.getJobOffersService = getJobOffersService;
    }

    @GetMapping("/levels/city/{city}/technology/{technology}")
    public List<CityTechnologyOfferDto> getOffersByCityAndTechnology(
            @PathVariable City city,
            @PathVariable Technology technology) {

        return getJobOffersService.getFilteredOffers(city, technology);
    }

}
