package com.hotel.booking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.booking.controller.dto.CreateBookingRequest;
import com.hotel.booking.entities.Booking;
import com.hotel.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(
            @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody CreateBookingRequest request) {

        return bookingService.createBooking(
                userId,
                request.getHotelId(),
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut()
        );
    }
  
    @GetMapping("/{bookingId}")
    public Booking getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

  
    @DeleteMapping("/{bookingId}")
    public Booking cancelBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-USER-ID") Long userId) {

        return bookingService.cancelBooking(bookingId, userId);
    }
    
    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(
            @PathVariable Long userId,
            @RequestHeader("X-USER-ID") Long requesterId) {

        if (!userId.equals(requesterId)) {
            throw new IllegalStateException("You can only view your own bookings");
        }

        return bookingService.getBookingsByUser(userId);
    }
    
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
