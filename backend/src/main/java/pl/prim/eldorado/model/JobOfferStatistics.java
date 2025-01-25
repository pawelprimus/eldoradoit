package pl.prim.eldorado.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "job_offer_statistics")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Change to Long

    private LocalDateTime fetchDate;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private Technology technology;

    @ElementCollection
    @CollectionTable(name = "offer_counts_mapping")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "count")
    private Map<ExperienceLevel, Integer> offerCounts;
}


