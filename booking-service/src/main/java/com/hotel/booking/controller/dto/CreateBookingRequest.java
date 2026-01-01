package com.hotel.booking.controller.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private Long roomId;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;
}
