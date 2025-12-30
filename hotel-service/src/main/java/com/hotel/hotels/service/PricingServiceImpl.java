package com.hotel.hotels.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.hotels.entity.CategoryPricing;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.entity.SeasonalPricing;
import com.hotel.hotels.repository.CategoryPricingRepository;
import com.hotel.hotels.repository.RoomCategoryRepository;
import com.hotel.hotels.repository.SeasonalPricingRepository;

@Service
@Transactional
public class PricingServiceImpl implements PricingService {

    private final CategoryPricingRepository pricingRepository;
    private final SeasonalPricingRepository seasonalRepository;
    private final RoomCategoryRepository categoryRepository;

    public PricingServiceImpl(CategoryPricingRepository pricingRepository,
                              SeasonalPricingRepository seasonalRepository,
                              RoomCategoryRepository categoryRepository) {
        this.pricingRepository = pricingRepository;
        this.seasonalRepository = seasonalRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryPricing setBasePricing(Long categoryId, CategoryPricing pricing) {
        RoomCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        pricing.setId(null);
        pricing.setCategory(category);

        return pricingRepository.save(pricing);
    }

    @Override
    public List<SeasonalPricing> addSeasonalPricing(Long categoryId, SeasonalPricing pricing) {
        RoomCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        pricing.setId(null);
        pricing.setCategory(category);
        pricing.setActive(true);

        seasonalRepository.save(pricing);
        return seasonalRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryPricing getBasePricing(Long categoryId) {
        return pricingRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Base pricing not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeasonalPricing> getSeasonalPricing(Long categoryId, LocalDate date) {
        return seasonalRepository
                .findByCategoryIdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        categoryId, date, date
                );
    }
}
