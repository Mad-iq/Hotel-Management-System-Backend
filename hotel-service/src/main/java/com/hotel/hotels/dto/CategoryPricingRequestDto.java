package com.hotel.hotels.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryPricingRequestDto {

    private BigDecimal basePrice;
    private String currency;
}
