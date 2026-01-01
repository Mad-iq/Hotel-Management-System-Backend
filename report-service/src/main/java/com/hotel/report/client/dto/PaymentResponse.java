package com.hotel.report.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {

    private Long id;
    private Long bookingId;
    private Long userId;

    private BigDecimal amount;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
