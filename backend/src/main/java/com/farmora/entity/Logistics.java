package com.farmora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "logistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Logistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    private String pickupSummary;   // e.g. "Farmer A (20km), Farmer B (30km)"
    private String deliveryLocation;

    private String vehicleType;
    private Double totalDistanceKm;
    private Integer estimatedTimeMinutes;
    private Double estimatedCost;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LogisticsStatus status = LogisticsStatus.PENDING;
}
