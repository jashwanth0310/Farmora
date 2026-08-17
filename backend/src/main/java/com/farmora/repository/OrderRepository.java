package com.farmora.repository;

import com.farmora.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);
}
