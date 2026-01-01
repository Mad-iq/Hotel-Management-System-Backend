package com.hotel.booking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.booking.client.HotelServiceClient;
import com.hotel.booking.client.dto.CategoryPricingDto;
import com.hotel.booking.client.dto.RoomDto;
import com.hotel.booking.client.dto.SeasonalPricingDto;
import com.hotel.booking.entities.Booking;
import com.hotel.booking.entities.BookingStatus;
import com.hotel.booking.repository.BookingRepository;

@Service

public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelServiceClient hotelServiceClient;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            HotelServiceClient hotelServiceClient) {
        this.bookingRepository = bookingRepository;
        this.hotelServiceClient = hotelServiceClient;
    }


    @Override
    public Booking createBooking(
            Long userId,
            Long hotelId,
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut) {

        List<RoomDto> rooms = hotelServiceClient.getRoomsByHotel(hotelId);

        RoomDto room = rooms.stream()
                .filter(r -> r.getId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Room not found in this hotel"));

        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new IllegalStateException("Room is not available");
        }

        boolean hasOverlap =
                !bookingRepository.findOverlappingBookings(
                        roomId,
                        checkIn,
                        checkOut,
                        BookingStatus.CONFIRMED
                ).isEmpty();

        if (hasOverlap) {
            throw new IllegalStateException("Room is already booked for the given dates");
        }

        BigDecimal totalAmount = calculatePrice(
                room.getCategoryId(),
                checkIn,
                checkOut
        );

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setHotelId(hotelId);
        booking.setRoomId(roomId);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        hotelServiceClient.updateRoomStatus(hotelId, roomId, "OCCUPIED");

        return savedBooking;
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    @Override
    public Booking cancelBooking(Long bookingId, Long userId) {

        Booking booking = getBookingById(bookingId);

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalStateException("You cannot cancel this booking");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        bookingRepository.save(booking);

        hotelServiceClient.updateRoomStatus(
                booking.getHotelId(),
                booking.getRoomId(),
                "AVAILABLE"
        );
        
        return savedBooking;
    }

    private BigDecimal calculatePrice(
            Long categoryId,
            LocalDate checkIn,
            LocalDate checkOut) {

        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();

        if (nights <= 0) {
            throw new IllegalArgumentException("Invalid booking dates");
        }

        CategoryPricingDto basePricing =
                hotelServiceClient.getBasePricing(categoryId);

        BigDecimal pricePerNight = basePricing.getBasePrice();

        // Optional seasonal override (simple version)
        List<SeasonalPricingDto> seasonalPrices =
                hotelServiceClient.getSeasonalPricing(categoryId, checkIn);

        if (!seasonalPrices.isEmpty()) {
            pricePerNight = seasonalPrices.get(0).getPrice();
        }

        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }
    
    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
