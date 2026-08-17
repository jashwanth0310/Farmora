package com.farmora.controller;

import com.farmora.entity.Buyer;
import com.farmora.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BuyerController {

    private final BuyerRepository buyerRepository;

    @GetMapping("/{id}")
    public Buyer getById(@PathVariable Long id) {
        return buyerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found: " + id));
    }

    @GetMapping("/by-user/{userId}")
    public Buyer getByUserId(@PathVariable Long userId) {
        return buyerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found for user: " + userId));
    }
}
