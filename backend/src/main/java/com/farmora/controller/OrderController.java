package com.farmora.controller;

import com.farmora.dto.CombinedOrderRequest;
import com.farmora.dto.OrderSummaryDTO;
import com.farmora.entity.Order;
import com.farmora.entity.OrderStatus;
import com.farmora.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    /** Creates a (possibly multi-farmer combined) order from selected supply. */
    @PostMapping
    public OrderSummaryDTO createOrder(@RequestBody CombinedOrderRequest req) {
        Order order = orderService.createCombinedOrder(req);
        return orderService.toSummary(order);
    }

    @GetMapping("/{id}")
    public OrderSummaryDTO getOrder(@PathVariable Long id) {
        return orderService.toSummary(orderService.getById(id));
    }

    @GetMapping("/buyer/{buyerId}")
    public List<OrderSummaryDTO> byBuyer(@PathVariable Long buyerId) {
        return orderService.getByBuyer(buyerId).stream()
                .map(orderService::toSummary)
                .collect(Collectors.toList());
    }

    /** Advance order through the tracking timeline (ORDER_CONFIRMED -> ... -> PAYMENT_RELEASED). */
    @PatchMapping("/{id}/status")
    public OrderSummaryDTO advanceStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order order = orderService.advanceStatus(id, status);
        return orderService.toSummary(order);
    }
}
