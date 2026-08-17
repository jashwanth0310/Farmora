package com.farmora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchedFarmerDTO {
    private Long matchId;
    private Long produceId;
    private Long farmerId;
    private String farmerName;
    private String village;
    private String crop;
    private Double availableQuantityKg;
    private Double pricePerKg;
    private String quality;
    private String availabilityDate;
    private Double distanceKm;
    private Double reliabilityScore;
    private Double matchScore; // 0-100, "AI match %"
}
