package com.hotel.hotels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.hotels.entity.CategoryPricing;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.entity.SeasonalPricing;
import com.hotel.hotels.repository.CategoryPricingRepository;
import com.hotel.hotels.repository.RoomCategoryRepository;
import com.hotel.hotels.repository.SeasonalPricingRepository;
import com.hotel.hotels.service.PricingServiceImpl;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private CategoryPricingRepository pricingRepository;

    @Mock
    private SeasonalPricingRepository seasonalRepository;

    @Mock
    private RoomCategoryRepository categoryRepository;

    @InjectMocks
    private PricingServiceImpl pricingService;

    @Test
    void setBasePricing_success() {
        RoomCategory category = new RoomCategory();
        category.setId(1L);

        CategoryPricing pricing = new CategoryPricing();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));
        when(pricingRepository.save(any(CategoryPricing.class)))
                .thenAnswer(i -> i.getArgument(0));

        CategoryPricing saved =
                pricingService.setBasePricing(1L, pricing);

        assertNotNull(saved.getCategory());
        assertEquals(category, saved.getCategory());
        verify(pricingRepository).save(saved);
    }

    @Test
    void getBasePricing_notFound_shouldThrowException() {
        when(pricingRepository.findByCategoryId(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                pricingService.getBasePricing(99L)
        );
    }

    @Test
    void getSeasonalPricing_success() {
        SeasonalPricing sp = new SeasonalPricing();

        when(seasonalRepository
                .findByCategoryIdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        eq(1L),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(List.of(sp));

        List<SeasonalPricing> result =
                pricingService.getSeasonalPricing(
                        1L,
                        LocalDate.now()
                );

        assertEquals(1, result.size());
        verify(seasonalRepository)
                .findByCategoryIdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        eq(1L),
                        any(LocalDate.class),
                        any(LocalDate.class));
    }
}
