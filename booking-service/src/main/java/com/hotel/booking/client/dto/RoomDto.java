package com.hotel.booking.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDto {

    private Long id;
    private Long hotelId;
    private Long categoryId;
    private String status; // AVAILABLE,OCCUPIED, MAINTENANce
}
