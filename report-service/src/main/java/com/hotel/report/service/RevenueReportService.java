package com.hotel.report.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.report.client.BookingClient;
import com.hotel.report.client.PaymentClient;
import com.hotel.report.client.dto.BookingResponse;
import com.hotel.report.client.dto.PaymentResponse;

@Service
public class RevenueReportService {

    private final BookingClient bookingClient;
    private final PaymentClient paymentClient;

    public RevenueReportService(
            BookingClient bookingClient,
            PaymentClient paymentClient) {
        this.bookingClient = bookingClient;
        this.paymentClient = paymentClient;
    }

    public BigDecimal calculateTotalRevenue() {

        List<BookingResponse> bookings =
                bookingClient.getAllBookings();

        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (BookingResponse booking : bookings) {

            System.out.println("Checking booking id = " + booking.getId());

            try {
                PaymentResponse payment =
                    paymentClient.getPaymentByBookingId(booking.getId());

                System.out.println("Payment FOUND for booking "
                        + booking.getId()
                        + " status = " + payment.getStatus()
                        + " amount = " + payment.getAmount());

                if ("PAID".equals(payment.getStatus())) {
                    totalRevenue = totalRevenue.add(payment.getAmount());
                    System.out.println("ADDED TO REVENUE");
                }

            } catch (Exception ex) {
                System.out.println("NO PAYMENT for booking "
                        + booking.getId()
                        + " reason = " + ex.getMessage());
            }
        }


        return totalRevenue;
    }
}
