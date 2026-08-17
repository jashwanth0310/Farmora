package com.farmora.service;

import com.farmora.entity.OrderStatus;
import com.farmora.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final FarmerRepository farmerRepository;
    private final BuyerRepository buyerRepository;
    private final ProduceRepository produceRepository;
    private final RequirementRepository requirementRepository;
    private final OrderRepository orderRepository;

    public Map<String, Object> overview() {
        Map<String, Object> stats = new HashMap<>();
        long totalFarmers = farmerRepository.count();
        long totalBuyers = buyerRepository.count();
        List<com.farmora.entity.Order> orders = orderRepository.findAll();

        long activeOrders = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.PAYMENT_RELEASED && o.getStatus() != OrderStatus.CANCELLED)
                .count();

        double gmv = orders.stream().mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0).sum();

        long delivered = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED
                        || o.getStatus() == OrderStatus.BUYER_CONFIRMED
                        || o.getStatus() == OrderStatus.PAYMENT_RELEASED)
                .count();

        double successRate = orders.isEmpty() ? 0 : Math.round((delivered * 1000.0) / orders.size()) / 10.0;

        stats.put("farmers", totalFarmers);
        stats.put("buyers", totalBuyers);
        stats.put("activeOrders", activeOrders);
        stats.put("gmv", gmv);
        stats.put("successfulDeliveryRate", successRate);
        stats.put("totalProduceListings", produceRepository.count());
        stats.put("totalRequirements", requirementRepository.count());
        return stats;
    }
}
