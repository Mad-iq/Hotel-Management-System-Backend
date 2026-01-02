package com.hotel.hotels.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryPricingRequestDto {

	@NotNull
    private BigDecimal basePrice;
	
	@NotNull
    private String currency;
}
