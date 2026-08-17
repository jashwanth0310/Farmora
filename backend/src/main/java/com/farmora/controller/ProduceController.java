package com.farmora.controller;

import com.farmora.dto.PostProduceRequest;
import com.farmora.dto.ProduceSearchResult;
import com.farmora.entity.Produce;
import com.farmora.service.ProduceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produce")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProduceController {

    private final ProduceService produceService;

    @PostMapping
    public Produce postProduce(@RequestBody PostProduceRequest req) {
        return produceService.postProduce(req);
    }

    @GetMapping("/farmer/{farmerId}")
    public List<Produce> byFarmer(@PathVariable Long farmerId) {
        return produceService.getByFarmer(farmerId);
    }

    @GetMapping
    public List<Produce> allActive() {
        return produceService.getActive();
    }

    /**
     * DISTANCE FINDER endpoint.
     * Buyer searches for farmers/produce; every result includes a computed
     * distanceKm and estimatedDeliveryTime from the buyer's location.
     *
     * Example: GET /api/produce/search?crop=Tomato&buyerLat=12.97&buyerLng=77.59&maxDistanceKm=50
     */
    @GetMapping("/search")
    public List<ProduceSearchResult> search(
            @RequestParam(required = false) String crop,
            @RequestParam Double buyerLat,
            @RequestParam Double buyerLng,
            @RequestParam(required = false) Double maxDistanceKm) {
        return produceService.searchForBuyer(crop, buyerLat, buyerLng, maxDistanceKm);
    }
}
