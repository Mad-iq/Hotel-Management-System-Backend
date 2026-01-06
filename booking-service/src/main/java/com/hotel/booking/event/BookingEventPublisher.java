package com.hotel.booking.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.hotel.booking.event.dto.BookingEvent;

@Component
public class BookingEventPublisher {

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public BookingEventPublisher(KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(BookingEvent event) {
        kafkaTemplate.send("booking-events", event);
    }
}
