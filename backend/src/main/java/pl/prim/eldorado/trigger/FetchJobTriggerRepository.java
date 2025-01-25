package pl.prim.eldorado.trigger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FetchJobTriggerRepository extends JpaRepository<FetchJobTrigger, Long> {

    Optional<FetchJobTrigger> findTopByOrderByTriggerTimeDesc();

}