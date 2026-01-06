package com.hotel.notification.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.hotel.notification.dto.BookingEvent;
import com.hotel.notification.dto.BookingEventType;
import com.hotel.notification.service.NotificationMailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BookingEventConsumer {

    private final NotificationMailService mailService;

    public BookingEventConsumer(NotificationMailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(
        topics = "booking-events",
        groupId = "notification-group-local"
    )
    public void handleBookingEvent(BookingEvent event) {

        log.info(
            "Received booking event: type={}, bookingId={}",
            event.getEventType(),
            event.getBookingId()
        );

        try {
        	switch (event.getEventType()) {

            case BOOKING_CREATED ->
                mailService.sendBookingConfirmation(event);

            case BOOKING_CANCELLED ->
                mailService.sendBookingCancellation(event);

            case CHECKIN_REMINDER ->
                mailService.sendCheckInReminder(event);

            case CHECKOUT_REMINDER ->
                mailService.sendCheckOutReminder(event);

            default ->
                log.warn(
                    "Unhandled booking event type: {} for bookingId={}",
                    event.getEventType(),
                    event.getBookingId()
                );
        }

        } catch (Exception ex) {
            log.error(
                "Email handling failed for bookingId={}, eventType={}",
                event.getBookingId(),
                event.getEventType(),
                ex
            );
        }
    }
}
