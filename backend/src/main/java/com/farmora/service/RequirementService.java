package com.farmora.service;

import com.farmora.dto.PostRequirementRequest;
import com.farmora.entity.Buyer;
import com.farmora.entity.Frequency;
import com.farmora.entity.Quality;
import com.farmora.entity.Requirement;
import com.farmora.repository.BuyerRepository;
import com.farmora.repository.RequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final BuyerRepository buyerRepository;

    public Requirement postRequirement(PostRequirementRequest req) {
        Buyer buyer = buyerRepository.findById(req.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found: " + req.getBuyerId()));

        Double lat = req.getDeliveryLatitude() != null ? req.getDeliveryLatitude() : buyer.getDeliveryLatitude();
        Double lng = req.getDeliveryLongitude() != null ? req.getDeliveryLongitude() : buyer.getDeliveryLongitude();

        Requirement requirement = Requirement.builder()
                .buyer(buyer)
                .crop(req.getCrop())
                .quantityKg(req.getQuantityKg())
                .frequency(Frequency.valueOf(req.getFrequency()))
                .requiredDate(req.getRequiredDate())
                .quality(Quality.valueOf(req.getQuality()))
                .maxPricePerKg(req.getMaxPricePerKg())
                .deliveryLocation(req.getDeliveryLocation() != null ? req.getDeliveryLocation() : buyer.getDeliveryLocation())
                .deliveryLatitude(lat)
                .deliveryLongitude(lng)
                .build();

        return requirementRepository.save(requirement);
    }

    public List<Requirement> getByBuyer(Long buyerId) {
        return requirementRepository.findByBuyerId(buyerId);
    }
}
