package com.farmora.repository;

import com.farmora.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    List<Requirement> findByBuyerId(Long buyerId);
}
