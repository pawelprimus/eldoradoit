package pl.prim.eldorado.getoffers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.prim.eldorado.model.OfferCountDto;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class JobOffersController {

    private final GetJobOffersService getJobOffersService;

    JobOffersController(GetJobOffersService getJobOffersService) {
        this.getJobOffersService = getJobOffersService;
    }

    @GetMapping
    public List<OfferCountDto> getAllOffers() {
        return getJobOffersService.getAll();
    }
}
