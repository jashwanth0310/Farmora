package com.farmora.controller;

import com.farmora.dto.LoginRequest;
import com.farmora.dto.RegisterBuyerRequest;
import com.farmora.dto.RegisterFarmerRequest;
import com.farmora.entity.Buyer;
import com.farmora.entity.Farmer;
import com.farmora.entity.User;
import com.farmora.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/farmer")
    public Farmer registerFarmer(@RequestBody RegisterFarmerRequest req) {
        return authService.registerFarmer(req);
    }

    @PostMapping("/register/buyer")
    public Buyer registerBuyer(@RequestBody RegisterBuyerRequest req) {
        return authService.registerBuyer(req);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest req) {
        return authService.login(req.getPhone(), req.getOtp());
    }
}
