package com.hotel.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingSummaryReportResponse {

    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;
}
