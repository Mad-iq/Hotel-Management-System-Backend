package com.hotel.hotels.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelResponseDto {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private Integer starRating;
}
