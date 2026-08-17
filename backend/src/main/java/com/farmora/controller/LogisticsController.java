package com.farmora.controller;

import com.farmora.entity.Logistics;
import com.farmora.entity.LogisticsStatus;
import com.farmora.service.LogisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LogisticsController {

    private final LogisticsService logisticsService;

    @GetMapping("/order/{orderId}")
    public Logistics byOrder(@PathVariable Long orderId) {
        return logisticsService.getByOrder(orderId);
    }

    @PatchMapping("/order/{orderId}/status")
    public Logistics updateStatus(@PathVariable Long orderId, @RequestParam LogisticsStatus status) {
        return logisticsService.updateStatus(orderId, status);
    }
}
