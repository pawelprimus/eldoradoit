package pl.prim.eldorado.helloworldstuff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    List<EndpointHit> findAllByOrderByHitTimeDesc();
}