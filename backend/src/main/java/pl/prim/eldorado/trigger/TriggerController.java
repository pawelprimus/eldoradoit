package pl.prim.eldorado.trigger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/trigger")
public class TriggerController {

    private final TriggerService triggerService;

    public TriggerController(TriggerService triggerService) {
        this.triggerService = triggerService;
    }


    @GetMapping("/newest")
    public LocalDateTime getNewest() {
        return triggerService.getNewestTriggerTime();
    }
}
