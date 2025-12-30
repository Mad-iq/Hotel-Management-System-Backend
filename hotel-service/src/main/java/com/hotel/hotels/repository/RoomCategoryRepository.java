package com.hotel.hotels.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.hotels.entity.RoomCategory;

public interface RoomCategoryRepository extends JpaRepository<RoomCategory, Long> {

    List<RoomCategory> findByHotelIdAndActiveTrue(Long hotelId);
}
