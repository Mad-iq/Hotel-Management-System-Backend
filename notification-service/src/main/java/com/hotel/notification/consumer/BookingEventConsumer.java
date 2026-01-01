package com.hotel.notification.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.hotel.notification.dto.BookingCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BookingEventConsumer {

    @KafkaListener(
        topics = "booking-events",
        groupId = "notification-group-local"
    )
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Received booking created event: {}", event);

        // Email sending will come here (next step)
    }
}
