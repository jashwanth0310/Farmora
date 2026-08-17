package com.farmora.controller;

import com.farmora.dto.MatchedFarmerDTO;
import com.farmora.entity.Requirement;
import com.farmora.repository.RequirementRepository;
import com.farmora.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MatchController {

    private final MatchingService matchingService;
    private final RequirementRepository requirementRepository;

    /** Returns AI-assisted matched farmers/produce for a requirement, ranked by match score (includes distance). */
    @GetMapping("/requirement/{requirementId}")
    public List<MatchedFarmerDTO> matchesForRequirement(@PathVariable Long requirementId) {
        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new IllegalArgumentException("Requirement not found: " + requirementId));
        return matchingService.matchRequirement(requirement);
    }
}
