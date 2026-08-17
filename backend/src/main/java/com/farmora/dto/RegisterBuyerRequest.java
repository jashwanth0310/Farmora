package com.farmora.dto;

import lombok.Data;

@Data
public class RegisterBuyerRequest {
    private String name;
    private String phone;
    private String email;
    private String businessName;
    private String businessType;
    private String deliveryLocation;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
}
