package com.farmora.repository;

import com.farmora.entity.Produce;
import com.farmora.entity.ProduceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduceRepository extends JpaRepository<Produce, Long> {
    List<Produce> findByFarmerId(Long farmerId);
    List<Produce> findByStatus(ProduceStatus status);
    List<Produce> findByCropIgnoreCaseAndStatus(String crop, ProduceStatus status);
}
