package com.hotel.booking.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCategoryDto {

    private Long id;
    private String name;
    private String description;
    private Integer capacity;
}
