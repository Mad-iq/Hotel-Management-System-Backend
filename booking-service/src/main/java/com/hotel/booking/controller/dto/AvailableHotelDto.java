package com.hotel.booking.controller.dto;


import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableHotelDto {
    private Long hotelId;
    private String name;
    private String city;
    private Integer starRating;
    private int availableRooms;
    private BigDecimal startingFromPrice;

}