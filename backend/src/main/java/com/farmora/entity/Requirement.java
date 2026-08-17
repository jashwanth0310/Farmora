package com.farmora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "requirements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    private String crop;
    private Double quantityKg;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    private LocalDate requiredDate;

    @Enumerated(EnumType.STRING)
    private Quality quality;

    private Double maxPricePerKg;

    private String deliveryLocation;
    private Double deliveryLatitude;
    private Double deliveryLongitude;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequirementStatus status = RequirementStatus.OPEN;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
