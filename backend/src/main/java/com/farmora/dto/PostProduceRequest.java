package com.farmora.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PostProduceRequest {
    private Long farmerId;
    private String crop;
    private Double quantityKg;
    private Double pricePerKg;
    private String quality; // GRADE_A, GRADE_B, STANDARD
    private LocalDate availabilityDate;
    private String locationText;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
}
