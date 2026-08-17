package com.farmora.dto;

import lombok.Data;

import java.util.List;

@Data
public class CombinedOrderRequest {
    private Long requirementId;
    private Long buyerId;
    private List<SelectedSupply> selections;

    @Data
    public static class SelectedSupply {
        private Long produceId;
        private Double quantityKg;
    }
}
