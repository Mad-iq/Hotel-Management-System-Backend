package com.hotel.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hotel.payment.client.dto.BookingResponse;

@FeignClient(name = "BOOKING-SERVICE")
public interface BookingClient {

    @GetMapping("/api/bookings/{bookingId}")
    BookingResponse getBookingById(@PathVariable("bookingId") Long bookingId);
}
