package com.farmora.dto;

import lombok.Data;

@Data
public class RegisterFarmerRequest {
    private String name;
    private String phone;
    private String village;
    private String district;
    private String state;
    private Double farmSize;
    private Double latitude;
    private Double longitude;
}
