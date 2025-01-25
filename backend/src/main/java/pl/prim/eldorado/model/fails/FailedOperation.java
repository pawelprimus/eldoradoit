package pl.prim.eldorado.model.fails;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// FailedOperation.java
@Entity
@Table(name = "failed_operations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Change to Long

    private LocalDateTime timestamp;

    private String operation;

    private String city;

    private String technology;

    private String errorMessage;

    private String errorType;

    private String stackTrace;

    @Builder.Default
    private int retryCount = 0;

    @Builder.Default
    private boolean resolved = false;

    private LocalDateTime resolvedAt;

    private String resolvedBy;

    // Create separate table for metadata
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id")
    private FailureMetadata metadata;
}

