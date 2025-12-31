package com.hotel.hotels.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCategoryResponseDto {

    private Long id;
    private String name;
    private Integer capacity;
    private String description;
}
