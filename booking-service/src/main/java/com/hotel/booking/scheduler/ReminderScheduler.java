package com.hotel.booking.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hotel.booking.entities.Booking;
import com.hotel.booking.entities.BookingStatus;
import com.hotel.booking.event.BookingEventPublisher;
import com.hotel.booking.event.dto.BookingEvent;
import com.hotel.booking.event.dto.BookingEventType;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.client.AuthServiceClient;
import com.hotel.booking.client.dto.UserProfileDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReminderScheduler {

    private final BookingRepository bookingRepository;
    private final BookingEventPublisher bookingEventPublisher;
    private final AuthServiceClient authServiceClient;

    public ReminderScheduler(
            BookingRepository bookingRepository,
            BookingEventPublisher bookingEventPublisher,
            AuthServiceClient authServiceClient) {

        this.bookingRepository = bookingRepository;
        this.bookingEventPublisher = bookingEventPublisher;
        this.authServiceClient = authServiceClient;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReminders() {

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        sendCheckInReminders(tomorrow);
        sendCheckOutReminders(today);
    }

    private void sendCheckInReminders(LocalDate reminderDate) {

        List<Booking> bookings =
                bookingRepository.findCheckInReminderCandidates(
                        BookingStatus.CONFIRMED,
                        reminderDate
                );

        for (Booking booking : bookings) {
            BookingEvent event = buildEvent(
                    booking,
                    BookingEventType.CHECKIN_REMINDER
            );
            bookingEventPublisher.publish(event);
        }

        log.info("Check-in reminders published for date={}", reminderDate);
    }

    private void sendCheckOutReminders(LocalDate today) {
        List<Booking> bookings =bookingRepository.findCheckOutReminderCandidates(
                        BookingStatus.CHECKED_IN,
                        today
                );

        for (Booking booking : bookings) {
            BookingEvent event = buildEvent(booking,BookingEventType.CHECKOUT_REMINDER);
            bookingEventPublisher.publish(event);
        }

        log.info("Check-out reminders published for date={}", today);
    }

    private BookingEvent buildEvent(Booking booking, BookingEventType type){
        UserProfileDto userProfile = authServiceClient.getProfileByUserId(booking.getUserId());
        BookingEvent event = new BookingEvent();
        event.setEventType(type);
        event.setBookingId(booking.getId());
        event.setUserId(booking.getUserId());
        event.setUserEmail(userProfile.getEmail()); 
        event.setHotelId(booking.getHotelId());
        event.setRoomId(booking.getRoomId());
        event.setCheckInDate(booking.getCheckInDate());
        event.setCheckOutDate(booking.getCheckOutDate());
        event.setTotalAmount(booking.getTotalAmount());
        event.setEventTime(java.time.LocalDateTime.now());
        return event;
    }

}
