package com.hotel.notification.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingEvent {

    private BookingEventType eventType;

    private Long bookingId;
    private Long userId;
    private String userEmail;

    private Long hotelId;
    private Long roomId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BigDecimal totalAmount;

    private LocalDateTime eventTime;
}
