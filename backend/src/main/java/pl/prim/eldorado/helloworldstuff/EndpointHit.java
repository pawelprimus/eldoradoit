package pl.prim.eldorado.helloworldstuff;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime hitTime;

    public EndpointHit() {
        this.hitTime = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public LocalDateTime getHitTime() {
        return hitTime;
    }
}