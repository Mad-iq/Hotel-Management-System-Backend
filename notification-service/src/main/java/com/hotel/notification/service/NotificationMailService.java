package com.hotel.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hotel.notification.dto.BookingEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationMailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(BookingEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject("Booking Confirmation");
        message.setText(
            "Booking confirmed!\n\n" +
            "Booking ID: " + event.getBookingId() + "\n" +
            "Hotel ID: " + event.getHotelId() + "\n" +
            "Amount: " + event.getTotalAmount()
        );

        mailSender.send(message);
        log.info(
            "Booking confirmation email sent for bookingId={}",
            event.getBookingId()
        );
    }
    
    public void sendBookingCancellation(BookingEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject("Booking Cancelled");
        message.setText(
            "Your booking has been cancelled.\n\n" +
            "Booking ID: " + event.getBookingId() + "\n" +
            "Hotel ID: " + event.getHotelId() + "\n" +
            "Check-in Date: " + event.getCheckInDate() + "\n" +
            "Check-out Date: " + event.getCheckOutDate()
        );

        mailSender.send(message);
        log.info(
            "Booking cancellation email sent for bookingId={}",
            event.getBookingId()
        );
    }
    
    public void sendCheckInReminder(BookingEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject("Upcoming Check-in Reminder");
        message.setText(
            "This is a reminder for your upcoming stay.\n\n" +
            "Booking ID: " + event.getBookingId() + "\n" +
            "Hotel ID: " + event.getHotelId() + "\n" +
            "Check-in Date: " + event.getCheckInDate() + "\n\n" +
            "We look forward to hosting you!"
        );

        mailSender.send(message);
        log.info(
            "Check-in reminder email sent for bookingId={}",
            event.getBookingId()
        );
    }

    public void sendCheckOutReminder(BookingEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getUserEmail());
        message.setSubject("Check-out Reminder");
        message.setText(
            "This is a reminder that your check-out is today.\n\n" +
            "Booking ID: " + event.getBookingId() + "\n" +
            "Hotel ID: " + event.getHotelId() + "\n" +
            "Check-out Date: " + event.getCheckOutDate() + "\n\n" +
            "We hope you had a pleasant stay!"
        );

        mailSender.send(message);
        log.info(
            "Check-out reminder email sent for bookingId={}",
            event.getBookingId()
        );
    }


}
