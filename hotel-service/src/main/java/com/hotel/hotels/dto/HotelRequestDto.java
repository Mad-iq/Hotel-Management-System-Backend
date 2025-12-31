package com.hotel.hotels.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRequestDto {

    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private Integer starRating;
}
