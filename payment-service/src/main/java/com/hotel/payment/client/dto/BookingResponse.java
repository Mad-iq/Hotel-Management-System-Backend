package com.hotel.payment.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponse {

    private Long id;
    private Long userId;
    private Long hotelId;
    private Long roomId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BigDecimal totalAmount;
    private String bookingStatus;
}
