package com.farmora.service;

import com.farmora.entity.*;
import com.farmora.repository.LogisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticsService {

    private final LogisticsRepository logisticsRepository;
    private final DistanceService distanceService;

    public Logistics createForOrder(Order order, double totalDistanceKm) {
        String pickupSummary = order.getOrderFarmers().stream()
                .map(of -> of.getFarmer().getUser().getName()
                        + " (" + (of.getDistanceKm() != null ? of.getDistanceKm() : "?") + " km)")
                .collect(Collectors.joining(", "));

        String vehicleType = totalDistanceKm > 60 ? "Mini Truck (Long Haul)" : "Mini Truck";

        Logistics logistics = Logistics.builder()
                .order(order)
                .pickupSummary(pickupSummary)
                .deliveryLocation(order.getBuyer().getDeliveryLocation())
                .vehicleType(vehicleType)
                .totalDistanceKm(Math.round(totalDistanceKm * 10.0) / 10.0)
                .estimatedTimeMinutes(parseMinutes(distanceService.estimatedTravelTime(totalDistanceKm)))
                .estimatedCost(distanceService.estimateTransportCost(totalDistanceKm))
                .status(LogisticsStatus.ASSIGNED)
                .build();

        return logisticsRepository.save(logistics);
    }

    public Logistics getByOrder(Long orderId) {
        return logisticsRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No logistics found for order " + orderId));
    }

    public Logistics updateStatus(Long orderId, LogisticsStatus status) {
        Logistics logistics = getByOrder(orderId);
        logistics.setStatus(status);
        return logisticsRepository.save(logistics);
    }

    private Integer parseMinutes(String humanReadable) {
        // best-effort parse of "Xh Ym" / "X min" back to total minutes for storage
        try {
            if (humanReadable.contains("h")) {
                String[] parts = humanReadable.replace("m", "").split("h");
                int h = Integer.parseInt(parts[0].trim());
                int m = parts.length > 1 && !parts[1].trim().isEmpty() ? Integer.parseInt(parts[1].trim()) : 0;
                return h * 60 + m;
            }
            return Integer.parseInt(humanReadable.replace("min", "").trim());
        } catch (Exception e) {
            return null;
        }
    }
}
