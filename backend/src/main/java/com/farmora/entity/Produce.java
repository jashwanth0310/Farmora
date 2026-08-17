package com.farmora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "produce")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    private String crop;

    private Double quantityKg;
    private Double availableQuantityKg;

    private Double pricePerKg;

    @Enumerated(EnumType.STRING)
    private Quality quality;

    private LocalDate availabilityDate;

    // location captured at time of posting (defaults to farmer's location)
    private String locationText;
    private Double latitude;
    private Double longitude;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProduceStatus status = ProduceStatus.ACTIVE;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (availableQuantityKg == null) availableQuantityKg = quantityKg;
    }
}
