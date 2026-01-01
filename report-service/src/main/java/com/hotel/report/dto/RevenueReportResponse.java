package com.hotel.report.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RevenueReportResponse {

    private BigDecimal totalRevenue;
    private String currency;
}
