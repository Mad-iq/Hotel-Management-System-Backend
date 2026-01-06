package com.hotel.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.payment.client.BookingClient;
import com.hotel.payment.client.dto.BookingResponse;
import com.hotel.payment.entity.Payment;
import com.hotel.payment.entity.PaymentStatus;
import com.hotel.payment.repository.PaymentRepository;
import com.hotel.payment.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private PaymentService paymentService;

    // ---------------- createPayment ----------------

    @Test
    void createPayment_success() {
        Long bookingId = 1L;
        Long userId = 10L;

        BookingResponse booking = new BookingResponse();
        booking.setBookingStatus("CONFIRMED");
        booking.setUserId(userId);
        booking.setTotalAmount(BigDecimal.valueOf(3000));

        when(paymentRepository.existsByBookingId(bookingId)).thenReturn(false);
        when(bookingClient.getBookingById(bookingId)).thenReturn(booking);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.createPayment(bookingId, userId);

        assertEquals(bookingId, payment.getBookingId());
        assertEquals(userId, payment.getUserId());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(BigDecimal.valueOf(3000), payment.getAmount());
    }

    @Test
    void createPayment_paymentAlreadyExists() {
        when(paymentRepository.existsByBookingId(1L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.createPayment(1L, 10L)
        );

        assertEquals("Payment already exists for this booking", ex.getMessage());
    }

    @Test
    void createPayment_bookingNotConfirmed() {
        BookingResponse booking = new BookingResponse();
        booking.setBookingStatus("CANCELLED");

        when(paymentRepository.existsByBookingId(1L)).thenReturn(false);
        when(bookingClient.getBookingById(1L)).thenReturn(booking);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.createPayment(1L, 10L)
        );

        assertEquals("Payment allowed only for CONFIRMED bookings", ex.getMessage());
    }

    @Test
    void createPayment_userMismatch() {
        BookingResponse booking = new BookingResponse();
        booking.setBookingStatus("CONFIRMED");
        booking.setUserId(99L);

        when(paymentRepository.existsByBookingId(1L)).thenReturn(false);
        when(bookingClient.getBookingById(1L)).thenReturn(booking);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.createPayment(1L, 10L)
        );

        assertEquals("You are not allowed to pay for this booking", ex.getMessage());
    }

    // ---------------- markPaymentAsPaid ----------------

    @Test
    void markPaymentAsPaid_success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setUserId(10L);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.markPaymentAsPaid(1L, 10L);

        assertEquals(PaymentStatus.PAID, result.getStatus());
        assertNotNull(result.getPaidAt());
    }

    @Test
    void markPaymentAsPaid_paymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.markPaymentAsPaid(1L, 10L)
        );

        assertEquals("Payment not found", ex.getMessage());
    }

    @Test
    void markPaymentAsPaid_userMismatch() {
        Payment payment = new Payment();
        payment.setUserId(99L);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.markPaymentAsPaid(1L, 10L)
        );

        assertEquals("You are not allowed to pay for this payment", ex.getMessage());
    }

    @Test
    void markPaymentAsPaid_alreadyPaid() {
        Payment payment = new Payment();
        payment.setUserId(10L);
        payment.setStatus(PaymentStatus.PAID);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.markPaymentAsPaid(1L, 10L)
        );

        assertEquals("Payment is already completed", ex.getMessage());
    }

    // ---------------- getPaymentByBookingId ----------------

    @Test
    void getPaymentByBookingId_success() {
        Payment payment = new Payment();
        payment.setBookingId(1L);
        payment.setUserId(10L);

        when(paymentRepository.findByBookingId(1L))
                .thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentByBookingId(1L, 10L);

        assertNotNull(result);
    }

    @Test
    void getPaymentByBookingId_notFound() {
        when(paymentRepository.findByBookingId(1L))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.getPaymentByBookingId(1L, null)
        );

        assertEquals("Payment not found for this booking", ex.getMessage());
    }

    @Test
    void getPaymentByBookingId_userMismatch() {
        Payment payment = new Payment();
        payment.setUserId(99L);

        when(paymentRepository.findByBookingId(1L))
                .thenReturn(Optional.of(payment));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.getPaymentByBookingId(1L, 10L)
        );

        assertEquals("You are not allowed to view this payment", ex.getMessage());
    }
}
