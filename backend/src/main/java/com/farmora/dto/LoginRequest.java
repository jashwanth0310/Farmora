package com.farmora.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String otp; // mock OTP, prototype accepts any 4-6 digit value
}
