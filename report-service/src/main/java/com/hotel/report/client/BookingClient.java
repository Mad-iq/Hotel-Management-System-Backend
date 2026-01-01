package com.hotel.report.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.hotel.report.client.dto.BookingResponse;

@FeignClient(name = "BOOKING-SERVICE")
public interface BookingClient {

    @GetMapping("/api/bookings")
    List<BookingResponse> getAllBookings();
}
