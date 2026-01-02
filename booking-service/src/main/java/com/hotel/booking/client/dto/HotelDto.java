package com.hotel.booking.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelDto {
    private Long id;
    private String name;
    private String city;
    private Integer starRating;

}
