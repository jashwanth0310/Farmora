package com.farmora.repository;

import com.farmora.entity.OrderFarmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderFarmerRepository extends JpaRepository<OrderFarmer, Long> {
    List<OrderFarmer> findByFarmerId(Long farmerId);
    List<OrderFarmer> findByOrderId(Long orderId);
}
