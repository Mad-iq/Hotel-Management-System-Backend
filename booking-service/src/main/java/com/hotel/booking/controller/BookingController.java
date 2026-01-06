package com.hotel.booking.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.booking.controller.dto.AvailableHotelDto;
import com.hotel.booking.controller.dto.AvailableRoomsByCategoryDto;
import com.hotel.booking.controller.dto.CreateBookingRequest;
import com.hotel.booking.controller.dto.SearchHotelsRequest;
import com.hotel.booking.entities.Booking;
import com.hotel.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody CreateBookingRequest request){
    	Booking booking = bookingService.createBooking(
                userId,
                authHeader,
                 request.getHotelId(),
                 request.getRoomId(),
                 request.getCheckIn(),
                request.getCheckOut());
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }
  
    @GetMapping("/{bookingId}")
    public Booking getBookingById(@PathVariable Long bookingId){
        return bookingService.getBookingById(bookingId);
    }
  
    @DeleteMapping("/{bookingId}")
    public Booking cancelBooking(@PathVariable Long bookingId,@RequestHeader("X-USER-ID") Long userId, @RequestHeader("Authorization") String authHeader){
        return bookingService.cancelBooking(bookingId, userId,authHeader);
    }
    
    @GetMapping("/user/mine")
    public List<Booking> getUserBookings(@RequestHeader("X-USER-ID") Long userId){
        return bookingService.getBookingsByUser(userId);
    }
    
    @GetMapping
    public List<Booking> getAllBookings(){
        return bookingService.getAllBookings();
    }
    
    @PostMapping("/search/hotels")
    public List<AvailableHotelDto> searchAvailableHotels(@Valid @RequestBody SearchHotelsRequest request){
        return bookingService.searchAvailableHotels(
                request.getCity(),
                 request.getCheckIn(),
                 request.getCheckOut(),
                request.getGuests());
    }
    
    @PostMapping("/{bookingId}/check-in")
    public Booking checkIn(@PathVariable Long bookingId){
        return bookingService.checkIn(bookingId);
    }

    @PostMapping("/{bookingId}/check-out")
    public Booking checkOut(@PathVariable Long bookingId){
        return bookingService.checkOut(bookingId);
    }
    
    @GetMapping("/available-rooms")
    public List<AvailableRoomsByCategoryDto> getAvailableRoomsByCategory(
            @RequestParam Long hotelId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam(required = false) Integer guests){
        return bookingService.getAvailableRoomsByCategory(hotelId,checkIn,checkOut,guests);
    }


}
