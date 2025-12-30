package com.hotel.hotels.service;

import java.util.List;

import com.hotel.hotels.entity.RoomCategory;

public interface RoomCategoryService {

    RoomCategory createCategory(Long hotelId, RoomCategory category);

    List<RoomCategory> getActiveCategoriesByHotel(Long hotelId);

    void deactivateCategory(Long categoryId);
}
