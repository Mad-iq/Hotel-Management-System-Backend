package com.hotel.report.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.report.client.BookingClient;
import com.hotel.report.client.dto.BookingResponse;
import com.hotel.report.dto.BookingSummaryReportResponse;

@Service
public class BookingSummaryReportService {

    private final BookingClient bookingClient;

    public BookingSummaryReportService(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    public BookingSummaryReportResponse getBookingSummary() {

        List<BookingResponse> bookings =
                bookingClient.getAllBookings();

        long total = bookings.size();

        long confirmed = bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getBookingStatus()))
                .count();

        long cancelled = bookings.stream()
                .filter(b -> "CANCELLED".equals(b.getBookingStatus()))
                .count();

        return new BookingSummaryReportResponse(
                total,
                confirmed,
                cancelled
        );
    }
}
