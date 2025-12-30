package com.hotel.hotels.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.repository.HotelRepository;
import com.hotel.hotels.repository.RoomCategoryRepository;

@Service
@Transactional
public class RoomCategoryServiceImpl implements RoomCategoryService {

    private final RoomCategoryRepository categoryRepository;
    private final HotelRepository hotelRepository;

    public RoomCategoryServiceImpl(RoomCategoryRepository categoryRepository,
                                   HotelRepository hotelRepository) {
        this.categoryRepository = categoryRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public RoomCategory createCategory(Long hotelId, RoomCategory category) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        category.setId(null);
        category.setHotel(hotel);
        category.setActive(true);

        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomCategory> getActiveCategoriesByHotel(Long hotelId) {
        return categoryRepository.findByHotelIdAndActiveTrue(hotelId);
    }

    @Override
    public void deactivateCategory(Long categoryId) {
        RoomCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setActive(false);
        categoryRepository.save(category);
    }
}
