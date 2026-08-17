package com.farmora.controller;

import com.farmora.entity.Payment;
import com.farmora.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public Payment byOrder(@PathVariable Long orderId) {
        return paymentService.getByOrder(orderId);
    }

    @PostMapping("/order/{orderId}/secure")
    public Payment secure(@PathVariable Long orderId) {
        return paymentService.secure(orderId);
    }

    @PostMapping("/order/{orderId}/release")
    public Payment release(@PathVariable Long orderId) {
        return paymentService.release(orderId);
    }
}
