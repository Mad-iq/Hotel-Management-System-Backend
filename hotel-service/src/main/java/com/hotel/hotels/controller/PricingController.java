package com.hotel.hotels.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hotel.hotels.dto.CategoryPricingRequestDto;
import com.hotel.hotels.dto.SeasonalPricingRequestDto;
import com.hotel.hotels.entity.CategoryPricing;
import com.hotel.hotels.entity.SeasonalPricing;
import com.hotel.hotels.service.PricingService;

@RestController
@RequestMapping("/api/categories/{categoryId}")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/pricing")
    public CategoryPricing setBasePricing(
            @PathVariable Long categoryId,
            @RequestBody CategoryPricingRequestDto dto) {

        CategoryPricing pricing = new CategoryPricing();
        pricing.setBasePrice(dto.getBasePrice());
        pricing.setCurrency(dto.getCurrency());

        return pricingService.setBasePricing(categoryId, pricing);
    }

    @PostMapping("/seasonal-pricing")
    public List<SeasonalPricing> addSeasonalPricing(
            @PathVariable Long categoryId,
            @RequestBody SeasonalPricingRequestDto dto) {

        SeasonalPricing pricing = new SeasonalPricing();
        pricing.setStartDate(dto.getStartDate());
        pricing.setEndDate(dto.getEndDate());
        pricing.setPrice(dto.getPrice());

        return pricingService.addSeasonalPricing(categoryId, pricing);
    }

    @GetMapping("/pricing")
    public CategoryPricing getBasePricing(@PathVariable Long categoryId) {
        return pricingService.getBasePricing(categoryId);
    }

    @GetMapping("/seasonal-pricing")
    public List<SeasonalPricing> getSeasonalPricing(
            @PathVariable Long categoryId,
            @RequestParam LocalDate date) {

        return pricingService.getSeasonalPricing(categoryId, date);
    }
}
