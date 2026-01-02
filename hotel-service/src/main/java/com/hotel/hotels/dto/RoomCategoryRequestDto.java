package com.hotel.hotels.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCategoryRequestDto {

	@NotNull
    private String name;
	
	@NotNull
    private Integer capacity;
	
	@NotNull
    private String description;
}
