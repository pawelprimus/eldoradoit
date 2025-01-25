package pl.prim.eldorado.model.stats;

import jakarta.persistence.*;
import lombok.*;
import pl.prim.eldorado.model.stats.enums.City;
import pl.prim.eldorado.model.stats.enums.ExperienceLevel;
import pl.prim.eldorado.model.stats.enums.Technology;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "job_offer_statistics")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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


