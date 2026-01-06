package com.hotel.booking.controller.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableRoomsByCategoryDto {

    private Long categoryId;
    private String categoryName;
    private String description;
    private Integer capacity;

    private BigDecimal pricePerNight;
    private String currency;

    private List<AvailableRoomSummaryDto> availableRooms;
}
