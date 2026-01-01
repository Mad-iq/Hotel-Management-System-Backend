package com.hotel.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.payment.entity.Payment;
import com.hotel.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 1️⃣ Create payment for a booking
    @PostMapping
    public Payment createPayment(
            @RequestParam Long bookingId,
            @RequestHeader("X-USER-ID") Long userId) {

        return paymentService.createPayment(bookingId, userId);
    }

    // 2️⃣ Mark payment as PAID
    @PutMapping("/{paymentId}/pay")
    public Payment pay(
            @PathVariable Long paymentId,
            @RequestHeader("X-USER-ID") Long userId) {

        return paymentService.markPaymentAsPaid(paymentId, userId);
    }

    // 3️⃣ Get payment by booking
    @GetMapping("/booking/{bookingId}")
    public Payment getPaymentByBooking(
            @PathVariable Long bookingId,
            @RequestHeader(value= "X-USER-ID", required = false) Long userId) {

        return paymentService.getPaymentByBookingId(bookingId, userId);
    }
}
