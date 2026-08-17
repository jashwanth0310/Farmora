package com.farmora.service;

import com.farmora.dto.RegisterBuyerRequest;
import com.farmora.dto.RegisterFarmerRequest;
import com.farmora.entity.*;
import com.farmora.repository.BuyerRepository;
import com.farmora.repository.FarmerRepository;
import com.farmora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final BuyerRepository buyerRepository;

    public Farmer registerFarmer(RegisterFarmerRequest req) {
        User user = userRepository.findByPhone(req.getPhone()).orElseGet(() ->
                userRepository.save(User.builder()
                        .name(req.getName())
                        .phone(req.getPhone())
                        .role(Role.FARMER)
                        .address(req.getVillage() + ", " + req.getDistrict() + ", " + req.getState())
                        .latitude(req.getLatitude())
                        .longitude(req.getLongitude())
                        .build()));

        return farmerRepository.findByUserId(user.getId()).orElseGet(() ->
                farmerRepository.save(Farmer.builder()
                        .user(user)
                        .village(req.getVillage())
                        .district(req.getDistrict())
                        .state(req.getState())
                        .farmSize(req.getFarmSize())
                        .build()));
    }

    public Buyer registerBuyer(RegisterBuyerRequest req) {
        User user = userRepository.findByPhone(req.getPhone()).orElseGet(() ->
                userRepository.save(User.builder()
                        .name(req.getName())
                        .phone(req.getPhone())
                        .email(req.getEmail())
                        .role(Role.BUYER)
                        .address(req.getDeliveryLocation())
                        .latitude(req.getDeliveryLatitude())
                        .longitude(req.getDeliveryLongitude())
                        .build()));

        return buyerRepository.findByUserId(user.getId()).orElseGet(() ->
                buyerRepository.save(Buyer.builder()
                        .user(user)
                        .businessName(req.getBusinessName())
                        .businessType(req.getBusinessType())
                        .deliveryLocation(req.getDeliveryLocation())
                        .deliveryLatitude(req.getDeliveryLatitude())
                        .deliveryLongitude(req.getDeliveryLongitude())
                        .build()));
    }

    /** Prototype-only mock OTP login: any phone that exists + any OTP logs in. */
    public User login(String phone, String otp) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("No account found for phone " + phone));
    }
}
