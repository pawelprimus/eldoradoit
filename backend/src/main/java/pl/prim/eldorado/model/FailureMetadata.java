package pl.prim.eldorado.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor // Add this
@AllArgsConstructor // Add this
public class FailureMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String correlationId;
    private String applicationVersion;
    private String environment;
    private String host;


    public static FailureMetadata empty() {
        return FailureMetadata.builder().build();
    }
}