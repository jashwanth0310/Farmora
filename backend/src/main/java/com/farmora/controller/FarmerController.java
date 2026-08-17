package com.farmora.controller;

import com.farmora.entity.Farmer;
import com.farmora.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FarmerController {

    private final FarmerRepository farmerRepository;

    @GetMapping("/{id}")
    public Farmer getById(@PathVariable Long id) {
        return farmerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found: " + id));
    }

    @GetMapping("/by-user/{userId}")
    public Farmer getByUserId(@PathVariable Long userId) {
        return farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found for user: " + userId));
    }
}
