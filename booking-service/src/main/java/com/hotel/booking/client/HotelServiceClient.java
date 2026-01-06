package com.hotel.booking.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.booking.client.dto.CategoryPricingDto;
import com.hotel.booking.client.dto.HotelDto;
import com.hotel.booking.client.dto.RoomCategoryDto;
import com.hotel.booking.client.dto.RoomDto;
import com.hotel.booking.client.dto.SeasonalPricingDto;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelServiceClient {

    @GetMapping("/api/hotels/{hotelId}/rooms")
    List<RoomDto> getRoomsByHotel(@PathVariable Long hotelId);

    @GetMapping("/api/categories/{categoryId}/pricing")
    CategoryPricingDto getBasePricing(@PathVariable Long categoryId);

    @GetMapping("/api/categories/{categoryId}/seasonal-pricing")
    List<SeasonalPricingDto> getSeasonalPricing(
        @PathVariable Long categoryId,
        @RequestParam("date") LocalDate date
    );

    @PutMapping("/api/hotels/{hotelId}/rooms/{roomId}/status")
    void updateRoomStatus(
        @PathVariable Long hotelId,
        @PathVariable Long roomId,
        @RequestParam("status") String status
    );
    
    
    //adding this for the search
    @GetMapping("/api/hotels")
    List<HotelDto> getAllHotels();
    
    //added this for room search
    @GetMapping("/api/hotels/{hotelId}/categories")
    List<RoomCategoryDto> getCategoriesByHotel(@PathVariable Long hotelId);
}

