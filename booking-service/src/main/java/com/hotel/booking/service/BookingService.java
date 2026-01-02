package com.hotel.booking.service;

import java.time.LocalDate;
import java.util.List;

import com.hotel.booking.controller.dto.AvailableHotelDto;
import com.hotel.booking.entities.Booking;

public interface BookingService {

    Booking createBooking(
            Long userId,
            Long hotelId,
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );

    Booking getBookingById(Long bookingId);

    Booking cancelBooking(Long bookingId, Long userId);

    List<Booking> getBookingsByUser(Long userId);
    
    List<Booking> getAllBookings();
    
    List<AvailableHotelDto> searchAvailableHotels(
            String city,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests
    );
    
    //for receptionist
    Booking checkIn(Long bookingId);
    Booking checkOut(Long bookingId);
}
