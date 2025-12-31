package com.hotel.hotels.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.hotel.hotels.dto.RoomCategoryRequestDto;
import com.hotel.hotels.dto.RoomCategoryResponseDto;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.service.RoomCategoryService;

@RestController
@RequestMapping("/api/hotels/{hotelId}/categories")
public class RoomCategoryController {

    private final RoomCategoryService categoryService;

    public RoomCategoryController(RoomCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public RoomCategoryResponseDto createCategory(
            @PathVariable Long hotelId,
            @RequestBody RoomCategoryRequestDto dto) {

        RoomCategory category = new RoomCategory();
        category.setName(dto.getName());
        category.setCapacity(dto.getCapacity());
        category.setDescription(dto.getDescription());

        RoomCategory saved = categoryService.createCategory(hotelId, category);
        return map(saved);
    }

    @GetMapping
    public List<RoomCategoryResponseDto> listCategories(@PathVariable Long hotelId) {
        return categoryService.getActiveCategoriesByHotel(hotelId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private RoomCategoryResponseDto map(RoomCategory c) {
        RoomCategoryResponseDto dto = new RoomCategoryResponseDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setCapacity(c.getCapacity());
        dto.setDescription(c.getDescription());
        return dto;
    }
}
