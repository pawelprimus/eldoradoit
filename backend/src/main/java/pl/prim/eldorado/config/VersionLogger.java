package pl.prim.eldorado.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VersionLogger {
    @Value("${app.version}")
    private String version;

    @PostConstruct
    public void logVersion() {
        log.info("Version: [{}]", version);
    }
}