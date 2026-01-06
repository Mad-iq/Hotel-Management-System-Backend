package com.hotel.hotels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.repository.HotelRepository;
import com.hotel.hotels.repository.RoomCategoryRepository;
import com.hotel.hotels.service.RoomCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class RoomCategoryServiceImplTest {

    @Mock
    private RoomCategoryRepository categoryRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private RoomCategoryServiceImpl roomCategoryService;

    @Test
    void createCategory_success() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        RoomCategory category = new RoomCategory();
        category.setName("Deluxe");

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));
        when(categoryRepository.save(any(RoomCategory.class)))
                .thenAnswer(i -> i.getArgument(0));

        RoomCategory saved =
                roomCategoryService.createCategory(1L, category);
        assertEquals(hotel, saved.getHotel());
        verify(categoryRepository).save(saved);
    }

    @Test
    void getActiveCategoriesByHotel_success() {
        RoomCategory category = new RoomCategory();
        category.setActive(true);

        when(categoryRepository.findByHotelIdAndActiveTrue(1L))
                .thenReturn(List.of(category));

        List<RoomCategory> result =
                roomCategoryService.getActiveCategoriesByHotel(1L);

        assertEquals(1, result.size());
        verify(categoryRepository)
                .findByHotelIdAndActiveTrue(1L);
    }

    @Test
    void deactivateCategory_success() {
        RoomCategory category = new RoomCategory();
        category.setActive(true);

        when(categoryRepository.findById(5L))
                .thenReturn(Optional.of(category));

        roomCategoryService.deactivateCategory(5L);
        verify(categoryRepository).save(category);
    }
}
