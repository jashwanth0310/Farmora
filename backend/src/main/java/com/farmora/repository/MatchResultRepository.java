package com.farmora.repository;

import com.farmora.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    List<MatchResult> findByRequirementIdOrderByMatchScoreDesc(Long requirementId);
}
