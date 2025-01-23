package pl.prim.eldorado;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Slf4j
public class EndpointHitController {

    private final EndpointHitService service;

    public EndpointHitController(EndpointHitService service) {
        this.service = service;
    }

    @GetMapping("/hits")
    public List<LocalDateTime> getHits() {
        // Save new hit
        log.info("New hit");
        service.saveHit(new EndpointHit());

        log.info("Hit saved");
        // Return all hits
        return service.getAllHits();
    }
}
