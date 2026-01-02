package com.hotel.payment.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.payment.client.BookingClient;
import com.hotel.payment.client.dto.BookingResponse;
import com.hotel.payment.entity.Payment;
import com.hotel.payment.entity.PaymentStatus;
import com.hotel.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingClient bookingClient) {
        this.paymentRepository = paymentRepository;
        this.bookingClient = bookingClient;
    }

    @Transactional
    public Payment createPayment(Long bookingId, Long userId) {

        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new IllegalStateException("Payment already exists for this booking");
        }

        BookingResponse booking = bookingClient.getBookingById(bookingId);

        if (!"CONFIRMED".equals(booking.getBookingStatus())) {
            throw new IllegalStateException("Payment allowed only for CONFIRMED bookings");
        }

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalStateException("You are not allowed to pay for this booking");
        }

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setUserId(userId);
        payment.setAmount(booking.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markPaymentAsPaid(Long paymentId, Long userId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment not found"));

        if (!payment.getUserId().equals(userId)) {
            throw new IllegalStateException("You are not allowed to pay for this payment");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is already completed");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Payment getPaymentByBookingId(Long bookingId, Long userId) {

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalStateException("Payment not found for this booking"));

        if (userId != null && !payment.getUserId().equals(userId)) {
            throw new IllegalStateException("You are not allowed to view this payment");
        }

        return payment;
    }
}
