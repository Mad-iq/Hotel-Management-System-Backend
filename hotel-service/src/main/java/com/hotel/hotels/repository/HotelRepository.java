package com.hotel.hotels.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.hotels.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByActiveTrue();

    List<Hotel> findByCityAndActiveTrue(String city);
}
