package com.hotel.hotels.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonalPricingRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
}
