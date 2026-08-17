package com.farmora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result returned to a BUYER when they search for produce/farmers.
 * Always includes a computed distanceKm from the buyer's delivery
 * location to the farmer/produce location - the "distance finder".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduceSearchResult {
    private Long produceId;
    private Long farmerId;
    private String farmerName;
    private String village;
    private String district;
    private String crop;
    private Double availableQuantityKg;
    private Double pricePerKg;
    private String quality;
    private String availabilityDate;
    private String locationText;
    private Double distanceKm;
    private String estimatedDeliveryTime; // human readable, derived from distance
    private Double reliabilityScore;
}
