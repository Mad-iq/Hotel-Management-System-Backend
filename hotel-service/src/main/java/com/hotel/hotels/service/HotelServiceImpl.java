package com.hotel.hotels.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.repository.HotelRepository;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        hotel.setId(null); // safety
        hotel.setActive(true);
        return hotelRepository.save(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> getAllActiveHotels() {
        return hotelRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Hotel getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    @Override
    public Hotel updateHotel(Long hotelId, Hotel updatedHotel) {
        Hotel existingHotel = getHotelById(hotelId);

        existingHotel.setName(updatedHotel.getName());
        existingHotel.setAddress(updatedHotel.getAddress());
        existingHotel.setCity(updatedHotel.getCity());
        existingHotel.setState(updatedHotel.getState());
        existingHotel.setCountry(updatedHotel.getCountry());
        existingHotel.setStarRating(updatedHotel.getStarRating());

        return hotelRepository.save(existingHotel);
    }

    @Override
    public void deactivateHotel(Long hotelId) {
        Hotel hotel = getHotelById(hotelId);
        hotel.setActive(false);
        hotelRepository.save(hotel);
    }
}
