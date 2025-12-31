package com.hotel.hotels.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hotel.hotels.dto.CategoryPricingRequestDto;
import com.hotel.hotels.dto.CategoryPricingResponseDto;
import com.hotel.hotels.dto.SeasonalPricingRequestDto;
import com.hotel.hotels.dto.SeasonalPricingResponseDto;
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
    public CategoryPricingResponseDto setBasePricing(
            @PathVariable Long categoryId,
            @RequestBody CategoryPricingRequestDto dto) {

        CategoryPricing pricing = new CategoryPricing();
        pricing.setBasePrice(dto.getBasePrice());
        pricing.setCurrency(dto.getCurrency());

        CategoryPricing saved = pricingService.setBasePricing(categoryId, pricing);

        return mapBasePricing(saved);
    }

    @GetMapping("/pricing")
    public CategoryPricingResponseDto getBasePricing(
            @PathVariable Long categoryId) {

        CategoryPricing pricing = pricingService.getBasePricing(categoryId);
        return mapBasePricing(pricing);
    }


    @PostMapping("/seasonal-pricing")
    public List<SeasonalPricingResponseDto> addSeasonalPricing(
            @PathVariable Long categoryId,
            @RequestBody SeasonalPricingRequestDto dto) {

        SeasonalPricing pricing = new SeasonalPricing();
        pricing.setStartDate(dto.getStartDate());
        pricing.setEndDate(dto.getEndDate());
        pricing.setPrice(dto.getPrice());

        return pricingService.addSeasonalPricing(categoryId, pricing)
                .stream()
                .map(this::mapSeasonalPricing)
                .toList();
    }

    @GetMapping("/seasonal-pricing")
    public List<SeasonalPricingResponseDto> getSeasonalPricing(
            @PathVariable Long categoryId,
            @RequestParam LocalDate date) {

        return pricingService.getSeasonalPricing(categoryId, date)
                .stream()
                .map(this::mapSeasonalPricing)
                .toList();
    }


    private CategoryPricingResponseDto mapBasePricing(CategoryPricing pricing) {
        CategoryPricingResponseDto dto = new CategoryPricingResponseDto();
        dto.setId(pricing.getId());
        dto.setBasePrice(pricing.getBasePrice());
        dto.setCurrency(pricing.getCurrency());
        dto.setCreatedAt(pricing.getCreatedAt());
        return dto;
    }

    private SeasonalPricingResponseDto mapSeasonalPricing(SeasonalPricing pricing) {
        SeasonalPricingResponseDto dto = new SeasonalPricingResponseDto();
        dto.setId(pricing.getId());
        dto.setStartDate(pricing.getStartDate());
        dto.setEndDate(pricing.getEndDate());
        dto.setPrice(pricing.getPrice());
        dto.setCreatedAt(pricing.getCreatedAt());
        return dto;
    }
}
