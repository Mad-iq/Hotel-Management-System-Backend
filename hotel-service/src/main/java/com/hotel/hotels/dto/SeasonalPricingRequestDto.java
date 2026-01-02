package com.hotel.hotels.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonalPricingRequestDto {

	@NotNull
    private LocalDate startDate;
	
	@NotNull
    private LocalDate endDate;
	
	@NotNull
    private BigDecimal price;
}
