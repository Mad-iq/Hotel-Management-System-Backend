package com.hotel.booking.client.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryPricingDto {

    private BigDecimal basePrice;
    private String currency;
}
