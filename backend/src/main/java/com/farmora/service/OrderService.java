package com.farmora.service;

import com.farmora.dto.CombinedOrderRequest;
import com.farmora.dto.OrderSummaryDTO;
import com.farmora.entity.*;
import com.farmora.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RequirementRepository requirementRepository;
    private final BuyerRepository buyerRepository;
    private final ProduceRepository produceRepository;
    private final DistanceService distanceService;
    private final LogisticsService logisticsService;
    private final PaymentService paymentService;

    private static final double PLATFORM_FEE_RATE = 0.02; // 2% platform fee

    /**
     * Creates an order, supporting MULTIPLE FARMER AGGREGATION:
     * a single buyer requirement can be fulfilled by combining supply
     * from several farmers/produce listings, as described in the spec.
     */
    @Transactional
    public Order createCombinedOrder(CombinedOrderRequest req) {
        Buyer buyer = buyerRepository.findById(req.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        Requirement requirement = req.getRequirementId() != null
                ? requirementRepository.findById(req.getRequirementId()).orElse(null)
                : null;

        String crop = null;
        double totalQuantity = 0;
        double produceValue = 0;
        double totalDistance = 0;
        List<OrderFarmer> orderFarmers = new ArrayList<>();

        Order order = Order.builder()
                .buyer(buyer)
                .requirement(requirement)
                .status(OrderStatus.ORDER_CONFIRMED)
                .build();

        for (CombinedOrderRequest.SelectedSupply sel : req.getSelections()) {
            Produce produce = produceRepository.findById(sel.getProduceId())
                    .orElseThrow(() -> new IllegalArgumentException("Produce not found: " + sel.getProduceId()));

            if (produce.getAvailableQuantityKg() < sel.getQuantityKg()) {
                throw new IllegalStateException("Insufficient quantity for produce " + produce.getId());
            }

            crop = produce.getCrop();
            totalQuantity += sel.getQuantityKg();
            produceValue += sel.getQuantityKg() * produce.getPricePerKg();

            Double distanceKm = distanceService.distanceKm(
                    buyer.getDeliveryLatitude(), buyer.getDeliveryLongitude(),
                    produce.getLatitude(), produce.getLongitude());
            totalDistance += (distanceKm != null ? distanceKm : 0);

            OrderFarmer of = OrderFarmer.builder()
                    .order(order)
                    .farmer(produce.getFarmer())
                    .produce(produce)
                    .quantityKg(sel.getQuantityKg())
                    .pricePerKg(produce.getPricePerKg())
                    .distanceKm(distanceKm)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();
            orderFarmers.add(of);

            // reduce farmer's available stock
            produce.setAvailableQuantityKg(produce.getAvailableQuantityKg() - sel.getQuantityKg());
            if (produce.getAvailableQuantityKg() <= 0) {
                produce.setStatus(ProduceStatus.SOLD_OUT);
            }
            produceRepository.save(produce);
        }

        double logisticsCost = distanceService.estimateTransportCost(totalDistance);
        double platformFee = Math.round(produceValue * PLATFORM_FEE_RATE);
        double totalAmount = produceValue + logisticsCost + platformFee;

        order.setCrop(crop);
        order.setTotalQuantityKg(totalQuantity);
        order.setProduceValue(produceValue);
        order.setLogisticsCost(logisticsCost);
        order.setPlatformFee(platformFee);
        order.setTotalAmount(totalAmount);
        order.setOrderFarmers(orderFarmers);

        Order saved = orderRepository.save(order);

        if (requirement != null) {
            requirement.setStatus(RequirementStatus.MATCHED);
            requirementRepository.save(requirement);
        }

        // auto-create logistics record for the combined pickup route
        logisticsService.createForOrder(saved, totalDistance);
        // initialize a pending payment record
        paymentService.initializePayment(saved);

        return saved;
    }

    public List<Order> getByBuyer(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    @Transactional
    public Order advanceStatus(Long orderId, OrderStatus newStatus) {
        Order order = getById(orderId);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public OrderSummaryDTO toSummary(Order order) {
        List<String> breakdown = order.getOrderFarmers().stream()
                .map(of -> of.getFarmer().getUser().getName() + ": " + of.getQuantityKg() + " kg"
                        + (of.getDistanceKm() != null ? " (" + of.getDistanceKm() + " km)" : ""))
                .collect(Collectors.toList());

        double totalDistance = order.getOrderFarmers().stream()
                .filter(of -> of.getDistanceKm() != null)
                .mapToDouble(OrderFarmer::getDistanceKm)
                .sum();

        return OrderSummaryDTO.builder()
                .orderId(order.getId())
                .crop(order.getCrop())
                .totalQuantityKg(order.getTotalQuantityKg())
                .produceValue(order.getProduceValue())
                .logisticsCost(order.getLogisticsCost())
                .platformFee(order.getPlatformFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .farmerBreakdown(breakdown)
                .totalDistanceKm(Math.round(totalDistance * 10.0) / 10.0)
                .build();
    }
}
