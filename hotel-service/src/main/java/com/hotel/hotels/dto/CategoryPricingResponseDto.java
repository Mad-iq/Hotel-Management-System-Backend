package com.hotel.hotels.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryPricingResponseDto {

    private Long id;
    private BigDecimal basePrice;
    private String currency;
    private LocalDateTime createdAt;
}
