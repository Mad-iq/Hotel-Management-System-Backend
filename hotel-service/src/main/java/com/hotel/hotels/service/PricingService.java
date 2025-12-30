package com.hotel.hotels.service;

import java.time.LocalDate;
import java.util.List;

import com.hotel.hotels.entity.CategoryPricing;
import com.hotel.hotels.entity.SeasonalPricing;

public interface PricingService {

    CategoryPricing setBasePricing(Long categoryId, CategoryPricing pricing);

    List<SeasonalPricing> addSeasonalPricing(Long categoryId, SeasonalPricing pricing);

    CategoryPricing getBasePricing(Long categoryId);

    List<SeasonalPricing> getSeasonalPricing(Long categoryId, LocalDate date);
}
