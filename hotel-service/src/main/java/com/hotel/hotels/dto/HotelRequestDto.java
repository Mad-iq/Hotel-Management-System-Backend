package com.hotel.hotels.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRequestDto {

	@NotNull
    private String name;
	
	@NotNull
    private String address;
	
	@NotNull
    private String city;
	
	@NotNull
    private String state;
	
	@NotNull
    private String country;
	
	@NotNull
    private Integer starRating;
}
