package com.farmora.service;

import com.farmora.dto.PostProduceRequest;
import com.farmora.dto.ProduceSearchResult;
import com.farmora.entity.Farmer;
import com.farmora.entity.Produce;
import com.farmora.entity.ProduceStatus;
import com.farmora.entity.Quality;
import com.farmora.repository.FarmerRepository;
import com.farmora.repository.ProduceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduceService {

    private final ProduceRepository produceRepository;
    private final FarmerRepository farmerRepository;
    private final DistanceService distanceService;

    public Produce postProduce(PostProduceRequest req) {
        Farmer farmer = farmerRepository.findById(req.getFarmerId())
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found: " + req.getFarmerId()));

        Double lat = req.getLatitude() != null ? req.getLatitude() : farmer.getUser().getLatitude();
        Double lng = req.getLongitude() != null ? req.getLongitude() : farmer.getUser().getLongitude();

        Produce produce = Produce.builder()
                .farmer(farmer)
                .crop(req.getCrop())
                .quantityKg(req.getQuantityKg())
                .availableQuantityKg(req.getQuantityKg())
                .pricePerKg(req.getPricePerKg())
                .quality(Quality.valueOf(req.getQuality()))
                .availabilityDate(req.getAvailabilityDate())
                .locationText(req.getLocationText() != null ? req.getLocationText() : farmer.getVillage())
                .latitude(lat)
                .longitude(lng)
                .imageUrl(req.getImageUrl())
                .status(ProduceStatus.ACTIVE)
                .build();

        return produceRepository.save(produce);
    }

    public List<Produce> getByFarmer(Long farmerId) {
        return produceRepository.findByFarmerId(farmerId);
    }

    public List<Produce> getActive() {
        return produceRepository.findByStatus(ProduceStatus.ACTIVE);
    }

    /**
     * DISTANCE FINDER — used when a BUYER searches for produce/farmers.
     * Computes distance from the buyer's delivery location to every
     * matching, active produce listing, and returns results sorted by
     * distance (nearest first). Optionally filtered by crop and a max
     * radius in km.
     */
    public List<ProduceSearchResult> searchForBuyer(String crop, Double buyerLat, Double buyerLng,
                                                      Double maxDistanceKm) {
        List<Produce> pool = (crop == null || crop.isBlank())
                ? produceRepository.findByStatus(ProduceStatus.ACTIVE)
                : produceRepository.findByCropIgnoreCaseAndStatus(crop, ProduceStatus.ACTIVE);

        return pool.stream()
                .filter(p -> p.getAvailableQuantityKg() != null && p.getAvailableQuantityKg() > 0)
                .map(p -> {
                    Double distanceKm = distanceService.distanceKm(buyerLat, buyerLng, p.getLatitude(), p.getLongitude());
                    Farmer farmer = p.getFarmer();
                    return ProduceSearchResult.builder()
                            .produceId(p.getId())
                            .farmerId(farmer.getId())
                            .farmerName(farmer.getUser().getName())
                            .village(farmer.getVillage())
                            .district(farmer.getDistrict())
                            .crop(p.getCrop())
                            .availableQuantityKg(p.getAvailableQuantityKg())
                            .pricePerKg(p.getPricePerKg())
                            .quality(p.getQuality() != null ? p.getQuality().name() : null)
                            .availabilityDate(p.getAvailabilityDate() != null ? p.getAvailabilityDate().toString() : null)
                            .locationText(p.getLocationText())
                            .distanceKm(distanceKm)
                            .estimatedDeliveryTime(distanceService.estimatedTravelTime(distanceKm))
                            .reliabilityScore(farmer.getReliabilityScore())
                            .build();
                })
                .filter(r -> maxDistanceKm == null || r.getDistanceKm() == null || r.getDistanceKm() <= maxDistanceKm)
                .sorted(Comparator.comparing(
                        ProduceSearchResult::getDistanceKm,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
}
