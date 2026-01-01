package com.hotel.booking.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonalPricingDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
}
