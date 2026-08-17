package com.farmora.controller;

import com.farmora.dto.PostRequirementRequest;
import com.farmora.entity.Requirement;
import com.farmora.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping
    public Requirement postRequirement(@RequestBody PostRequirementRequest req) {
        return requirementService.postRequirement(req);
    }

    @GetMapping("/buyer/{buyerId}")
    public List<Requirement> byBuyer(@PathVariable Long buyerId) {
        return requirementService.getByBuyer(buyerId);
    }
}
