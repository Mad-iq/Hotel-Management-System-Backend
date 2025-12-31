package com.hotel.hotels.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.hotel.hotels.dto.HotelRequestDto;
import com.hotel.hotels.dto.HotelResponseDto;
import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.service.HotelService;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    public HotelResponseDto createHotel(@RequestBody HotelRequestDto dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setState(dto.getState());
        hotel.setCountry(dto.getCountry());
        hotel.setStarRating(dto.getStarRating());

        Hotel saved = hotelService.createHotel(hotel);
        return mapToResponse(saved);
    }

    @GetMapping
    public List<HotelResponseDto> getAllHotels() {
        return hotelService.getAllActiveHotels()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HotelResponseDto mapToResponse(Hotel hotel) {
        HotelResponseDto dto = new HotelResponseDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());
        dto.setCity(hotel.getCity());
        dto.setState(hotel.getState());
        dto.setCountry(hotel.getCountry());
        dto.setStarRating(hotel.getStarRating());
        return dto;
    }
}
