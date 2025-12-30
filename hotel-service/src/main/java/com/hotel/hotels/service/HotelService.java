package com.hotel.hotels.service;

import java.util.List;

import com.hotel.hotels.entity.Hotel;

public interface HotelService {

    Hotel createHotel(Hotel hotel);

    List<Hotel> getAllActiveHotels();

    Hotel getHotelById(Long hotelId);

    Hotel updateHotel(Long hotelId, Hotel updatedHotel);

    void deactivateHotel(Long hotelId);
}
