package com.hotel.hotels.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.hotels.entity.SeasonalPricing;

public interface SeasonalPricingRepository extends JpaRepository<SeasonalPricing, Long> {

    List<SeasonalPricing> findByCategoryIdAndActiveTrue(Long categoryId);

    List<SeasonalPricing> findByCategoryIdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long categoryId,
            LocalDate date1,
            LocalDate date2
    );
}
