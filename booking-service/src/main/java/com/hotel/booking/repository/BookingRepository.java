package com.hotel.booking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.booking.entities.Booking;
import com.hotel.booking.entities.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.roomId = :roomId
          AND b.bookingStatus IN :statuses
          AND b.checkInDate < :checkOut
          AND b.checkOutDate > :checkIn
    """)
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
           @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("statuses") List<BookingStatus> statuses);
    
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
