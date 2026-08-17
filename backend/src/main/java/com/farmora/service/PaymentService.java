package com.farmora.service;

import com.farmora.entity.*;
import com.farmora.repository.OrderFarmerRepository;
import com.farmora.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mock payment flow only, per spec section 16 - no real money/escrow
 * integration for the prototype.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderFarmerRepository orderFarmerRepository;

    public Payment initializePayment(Order order) {
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();
        return paymentRepository.save(payment);
    }

    public Payment secure(Long orderId) {
        Payment payment = getByOrder(orderId);
        payment.setStatus(PaymentStatus.SECURED);
        return paymentRepository.save(payment);
    }

    public Payment release(Long orderId) {
        Payment payment = getByOrder(orderId);
        payment.setStatus(PaymentStatus.RELEASED);
        payment.setReleasedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        List<OrderFarmer> farmers = orderFarmerRepository.findByOrderId(orderId);
        farmers.forEach(of -> of.setPaymentStatus(PaymentStatus.RELEASED));
        orderFarmerRepository.saveAll(farmers);

        return saved;
    }

    public Payment getByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No payment found for order " + orderId));
    }
}
