package pl.prim.eldorado.trigger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class TriggerService {

    private final FetchJobTriggerRepository repository;

    TriggerService(FetchJobTriggerRepository repository) {
        this.repository = repository;
    }

     public void fetchWasTriggered(TriggerType type) {
        FetchJobTrigger trigger = new FetchJobTrigger().builder()
                .triggerTime(LocalDateTime.now())
                .triggerType(type)
                .build();

        FetchJobTrigger saved = repository.save(trigger);
        log.info("Trigger saved [{}, {}]", saved.getId(), saved.getTriggerTime());
    }

    public LocalDateTime getNewestTriggerTime() {
        Optional<FetchJobTrigger> trigger = repository.findTopByOrderByTriggerTimeDesc();
        if(trigger.isPresent()){
            return trigger.get().getTriggerTime();
        }
        return LocalDateTime.MIN;
    }
}
