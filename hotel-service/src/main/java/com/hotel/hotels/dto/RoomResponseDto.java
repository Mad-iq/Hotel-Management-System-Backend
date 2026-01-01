package com.hotel.hotels.dto;

import com.hotel.hotels.entity.RoomStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomResponseDto {

    private Long id;
    private String roomNumber;
    private RoomStatus status;
    private Long hotelId;
    private Long categoryId;
}
