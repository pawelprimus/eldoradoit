package pl.prim.eldorado;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EndpointHitController {

    private final EndpointHitRepository repository;

    public EndpointHitController(EndpointHitRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/hits")
    public List<LocalDateTime> getHits() {
        // Save new hit
        repository.save(new EndpointHit());

        // Return all hits
        return repository.findAllByOrderByHitTimeDesc()
                .stream()
                .map(EndpointHit::getHitTime)
                .collect(Collectors.toList());
    }
}
