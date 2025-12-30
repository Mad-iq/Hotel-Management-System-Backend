package com.hotel.hotels.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.hotels.entity.CategoryPricing;

public interface CategoryPricingRepository extends JpaRepository<CategoryPricing, Long> {

    Optional<CategoryPricing> findByCategoryId(Long categoryId);
}
