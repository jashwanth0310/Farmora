package com.farmora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long orderId;
    private String crop;
    private Double totalQuantityKg;
    private Double produceValue;
    private Double logisticsCost;
    private Double platformFee;
    private Double totalAmount;
    private String status;
    private List<String> farmerBreakdown;
    private Double totalDistanceKm;
}
