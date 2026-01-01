package com.hotel.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hotel.notification.dto.BookingCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationMailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(BookingCreatedEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("debashritamandal852@gmail.com");
        message.setSubject("Booking Confirmation");
        message.setText(
            "Booking confirmed!\n\n" +
            "Booking ID: " + event.getBookingId() + "\n" +
            "Hotel ID: " + event.getHotelId() + "\n" +
            "Amount: " + event.getTotalAmount()
        );

        mailSender.send(message);
        log.info("Booking confirmation email sent for bookingId={}", event.getBookingId());
    }
}
