package com.hotel.hotels.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequestDto {

	@NotNull
    private String roomNumber;
}
