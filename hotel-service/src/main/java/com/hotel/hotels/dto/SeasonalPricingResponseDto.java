package com.hotel.hotels.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonalPricingResponseDto {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private LocalDateTime createdAt;
}
