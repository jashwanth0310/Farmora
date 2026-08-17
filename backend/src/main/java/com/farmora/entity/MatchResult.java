package com.farmora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "match_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    @ManyToOne
    @JoinColumn(name = "produce_id", nullable = false)
    private Produce produce;

    private Double matchScore;      // 0-100
    private Double distanceKm;      // computed via DistanceService
    private Double quantityMatchedKg;

    @Builder.Default
    private String status = "SUGGESTED"; // SUGGESTED, SELECTED
}
