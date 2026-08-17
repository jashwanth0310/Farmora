package com.farmora.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PostRequirementRequest {
    private Long buyerId;
    private String crop;
    private Double quantityKg;
    private String frequency; // ONE_TIME, DAILY, ALTERNATE_DAYS, WEEKLY, CUSTOM
    private LocalDate requiredDate;
    private String quality;
    private Double maxPricePerKg;
    private String deliveryLocation;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
}
