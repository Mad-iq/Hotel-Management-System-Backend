package com.hotel.booking.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.hotel.booking.event.dto.BookingCreatedEvent;

@Component
public class BookingEventPublisher {

    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    public BookingEventPublisher(
            KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBookingCreated(BookingCreatedEvent event) {
        kafkaTemplate.send("booking-events", event);
    }
}
