package pl.prim.eldorado.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.prim.eldorado.fetchoffers.JobOfferStatisticsService;
import pl.prim.eldorado.trigger.TriggerService;

import static pl.prim.eldorado.trigger.TriggerType.MANUAL;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final JobOfferStatisticsService statisticsService;
    private final TriggerService triggerService;

    @Value("${app.admin.secret-key}")
    private String secretKey;

    public AdminController(JobOfferStatisticsService statisticsService, TriggerService triggerService) {
        this.statisticsService = statisticsService;
        this.triggerService = triggerService;
    }

    @PostMapping("/fetch-jobs")
    public ResponseEntity<String> triggerJobFetch(@RequestHeader("X-Admin-Key") String key) {
        if (!secretKey.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        triggerService.fetchWasTriggered(MANUAL);

        statisticsService.collectStatisticsForAllCombinations().subscribe();
        return ResponseEntity.accepted().body("Job fetch started");
    }
}
