package com.hotel.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hotel.report.client.dto.PaymentResponse;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @GetMapping("/api/payments/booking/{bookingId}")
    PaymentResponse getPaymentByBookingId(
            @PathVariable("bookingId") Long bookingId);
}
