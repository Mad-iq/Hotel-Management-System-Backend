package com.hotel.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.report.client.BookingClient;
import com.hotel.report.client.dto.BookingResponse;
import com.hotel.report.dto.BookingSummaryReportResponse;
import com.hotel.report.service.BookingSummaryReportService;

@ExtendWith(MockitoExtension.class)
class BookingSummaryReportServiceTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingSummaryReportService bookingSummaryReportService;

    @Test
    void getBookingSummary_countsCorrectly() {
        BookingResponse b1 = new BookingResponse();
        b1.setBookingStatus("CONFIRMED");

        BookingResponse b2 = new BookingResponse();
        b2.setBookingStatus("CANCELLED");

        BookingResponse b3 = new BookingResponse();
        b3.setBookingStatus("CONFIRMED");

        when(bookingClient.getAllBookings())
                .thenReturn(List.of(b1, b2, b3));

        BookingSummaryReportResponse response =
                bookingSummaryReportService.getBookingSummary();

        assertEquals(3, response.getTotalBookings());
        assertEquals(2, response.getConfirmedBookings());
        assertEquals(1, response.getCancelledBookings());
    }

    @Test
    void getBookingSummary_noBookings() {
        when(bookingClient.getAllBookings())
                .thenReturn(List.of());

        BookingSummaryReportResponse response =
                bookingSummaryReportService.getBookingSummary();

        assertEquals(0, response.getTotalBookings());
        assertEquals(0, response.getConfirmedBookings());
        assertEquals(0, response.getCancelledBookings());
    }
}
