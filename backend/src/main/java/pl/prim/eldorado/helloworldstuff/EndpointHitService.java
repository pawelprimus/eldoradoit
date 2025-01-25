package pl.prim.eldorado.helloworldstuff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
class EndpointHitService {

    private final EndpointHitRepository repository;

    EndpointHitService(EndpointHitRepository repository) {
        this.repository = repository;
    }


    void saveHit(EndpointHit endpointHit) {
        log.info("New hit [{}, {}]", endpointHit.getId(), endpointHit.getHitTime());
        EndpointHit saved = repository.save(endpointHit);
        log.info("Hit saved [{}, {}]", saved.getId(), saved.getHitTime());
    }

    List<LocalDateTime> getAllHits() {
        log.info("Getting all hits");
       return repository.findAllByOrderByHitTimeDesc()
                .stream()
                .map(EndpointHit::getHitTime)
                .collect(Collectors.toList());
    }
}
