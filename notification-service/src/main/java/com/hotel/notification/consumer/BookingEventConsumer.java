package com.hotel.notification.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.hotel.notification.dto.BookingCreatedEvent;
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
    	public void handleBookingCreated(BookingCreatedEvent event) {

    	    log.info("Received booking created event: {}", event);

    	    try {
    	        mailService.sendBookingConfirmation(event);
    	    } catch (Exception ex) {
    	        log.error(
    	            "Email failed for bookingId={}, userId={}",
    	            event.getBookingId(),
    	            event.getUserId(),
    	            ex
    	        );
    	    }
    	}

}
