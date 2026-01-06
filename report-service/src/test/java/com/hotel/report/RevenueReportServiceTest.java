package com.hotel.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.report.client.BookingClient;
import com.hotel.report.client.PaymentClient;
import com.hotel.report.client.dto.BookingResponse;
import com.hotel.report.client.dto.PaymentResponse;
import com.hotel.report.service.RevenueReportService;

@ExtendWith(MockitoExtension.class)
class RevenueReportServiceTest {

    @Mock
    private BookingClient bookingClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private RevenueReportService revenueReportService;

    @Test
    void calculateTotalRevenue_onlyPaidPaymentsAreCounted() {
        BookingResponse b1 = new BookingResponse();
        b1.setId(1L);

        BookingResponse b2 = new BookingResponse();
        b2.setId(2L);

        when(bookingClient.getAllBookings())
                .thenReturn(List.of(b1, b2));

        PaymentResponse paidPayment = new PaymentResponse();
        paidPayment.setStatus("PAID");
        paidPayment.setAmount(BigDecimal.valueOf(2000));

        PaymentResponse pendingPayment = new PaymentResponse();
        pendingPayment.setStatus("PENDING");
        pendingPayment.setAmount(BigDecimal.valueOf(5000));

        when(paymentClient.getPaymentByBookingId(1L))
                .thenReturn(paidPayment);

        when(paymentClient.getPaymentByBookingId(2L))
                .thenReturn(pendingPayment);

        BigDecimal revenue = revenueReportService.calculateTotalRevenue();

        assertEquals(BigDecimal.valueOf(2000), revenue);
    }

    @Test
    void calculateTotalRevenue_paymentNotFound_isIgnored() {
        BookingResponse booking = new BookingResponse();
        booking.setId(1L);

        when(bookingClient.getAllBookings())
                .thenReturn(List.of(booking));

        when(paymentClient.getPaymentByBookingId(1L))
                .thenThrow(new RuntimeException("Payment not found"));

        BigDecimal revenue = revenueReportService.calculateTotalRevenue();

        assertEquals(BigDecimal.ZERO, revenue);
    }

    @Test
    void calculateTotalRevenue_noBookings() {
        when(bookingClient.getAllBookings())
                .thenReturn(List.of());

        BigDecimal revenue = revenueReportService.calculateTotalRevenue();

        assertEquals(BigDecimal.ZERO, revenue);
    }
}
