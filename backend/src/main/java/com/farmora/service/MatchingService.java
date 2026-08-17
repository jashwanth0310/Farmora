package com.farmora.service;

import com.farmora.dto.MatchedFarmerDTO;
import com.farmora.entity.*;
import com.farmora.repository.MatchResultRepository;
import com.farmora.repository.ProduceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rule-based "AI-assisted" matching engine described in the Farmora spec.
 *
 * Match Score = 0.30*QuantityScore + 0.25*PriceScore + 0.20*DistanceScore
 *             + 0.15*QualityScore  + 0.10*AvailabilityScore
 */
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final ProduceRepository produceRepository;
    private final MatchResultRepository matchResultRepository;
    private final DistanceService distanceService;

    public List<MatchedFarmerDTO> matchRequirement(Requirement requirement) {
        List<Produce> candidates = produceRepository
                .findByCropIgnoreCaseAndStatus(requirement.getCrop(), ProduceStatus.ACTIVE);

        return candidates.stream()
                .filter(p -> p.getAvailableQuantityKg() != null && p.getAvailableQuantityKg() > 0)
                .map(p -> buildMatch(requirement, p))
                .sorted(Comparator.comparing(MatchedFarmerDTO::getMatchScore).reversed())
                .collect(Collectors.toList());
    }

    private MatchedFarmerDTO buildMatch(Requirement req, Produce produce) {
        double quantityScore = quantityScore(req.getQuantityKg(), produce.getAvailableQuantityKg());
        double priceScore = priceScore(req.getMaxPricePerKg(), produce.getPricePerKg());

        Double distanceKm = distanceService.distanceKm(
                req.getDeliveryLatitude(), req.getDeliveryLongitude(),
                produce.getLatitude(), produce.getLongitude());
        double distanceScore = distanceService.distanceScore(distanceKm);

        double qualityScore = qualityScore(req.getQuality(), produce.getQuality());
        double availabilityScore = availabilityScore(req.getRequiredDate(), produce.getAvailabilityDate());

        double finalScore = (0.30 * quantityScore)
                + (0.25 * priceScore)
                + (0.20 * distanceScore)
                + (0.15 * qualityScore)
                + (0.10 * availabilityScore);

        double matchPercent = Math.round(finalScore * 1000.0) / 10.0; // one decimal %

        MatchResult saved = matchResultRepository.save(MatchResult.builder()
                .requirement(req)
                .produce(produce)
                .matchScore(matchPercent)
                .distanceKm(distanceKm)
                .quantityMatchedKg(Math.min(req.getQuantityKg(), produce.getAvailableQuantityKg()))
                .status("SUGGESTED")
                .build());

        Farmer farmer = produce.getFarmer();

        return MatchedFarmerDTO.builder()
                .matchId(saved.getId())
                .produceId(produce.getId())
                .farmerId(farmer.getId())
                .farmerName(farmer.getUser().getName())
                .village(farmer.getVillage())
                .crop(produce.getCrop())
                .availableQuantityKg(produce.getAvailableQuantityKg())
                .pricePerKg(produce.getPricePerKg())
                .quality(produce.getQuality() != null ? produce.getQuality().name() : null)
                .availabilityDate(produce.getAvailabilityDate() != null ? produce.getAvailabilityDate().toString() : null)
                .distanceKm(distanceKm)
                .reliabilityScore(farmer.getReliabilityScore())
                .matchScore(matchPercent)
                .build();
    }

    private double quantityScore(Double required, Double available) {
        if (required == null || required == 0 || available == null) return 0;
        double ratio = available / required;
        return Math.min(ratio, 1.0); // full score once supply covers demand
    }

    private double priceScore(Double maxPrice, Double offeredPrice) {
        if (maxPrice == null || offeredPrice == null || maxPrice <= 0) return 0.5;
        if (offeredPrice > maxPrice) {
            // penalize but don't zero out completely - still visible as a lower match
            double overBy = (offeredPrice - maxPrice) / maxPrice;
            return Math.max(0, 1.0 - overBy);
        }
        // the cheaper relative to max price, the better - capped benefit
        double savings = (maxPrice - offeredPrice) / maxPrice;
        return Math.min(1.0, 0.7 + savings);
    }

    private double qualityScore(Quality required, Quality offered) {
        if (required == null || offered == null) return 0.5;
        if (required == offered) return 1.0;
        // Grade A > Grade B > Standard - partial credit if offered is higher grade
        int reqRank = rank(required);
        int offRank = rank(offered);
        if (offRank >= reqRank) return 0.85;
        return 0.4;
    }

    private int rank(Quality q) {
        return switch (q) {
            case GRADE_A -> 3;
            case GRADE_B -> 2;
            case STANDARD -> 1;
        };
    }

    private double availabilityScore(java.time.LocalDate required, java.time.LocalDate available) {
        if (required == null || available == null) return 0.5;
        if (!available.isAfter(required)) return 1.0; // available on/before required date
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(required, available);
        if (daysLate <= 1) return 0.6;
        return 0.2;
    }
}
