package pl.prim.eldorado.getoffers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.prim.eldorado.getoffers.dto.CityTechnologyOfferDto;
import pl.prim.eldorado.getoffers.dto.OfferCountDto;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobOffersController {

    private final GetJobOffersService getJobOffersService;

    JobOffersController(GetJobOffersService getJobOffersService) {
        this.getJobOffersService = getJobOffersService;
    }

    @GetMapping("/all")
    public List<OfferCountDto> getAllOffers() {
        return getJobOffersService.getAll();
    }

    @GetMapping("/levels")
    public List<CityTechnologyOfferDto> getOffersWithLevels() {
        return getJobOffersService.getOffersWithLevels();
    }
}
