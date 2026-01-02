package com.hotel.booking.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.booking.client.HotelServiceClient;
import com.hotel.booking.entities.Booking;
import com.hotel.booking.entities.BookingStatus;
import com.hotel.booking.repository.BookingRepository;

@Component
public class NoShowScheduler {

    private final BookingRepository bookingRepository;
    private final HotelServiceClient hotelServiceClient;

    public NoShowScheduler(
            BookingRepository bookingRepository,
            HotelServiceClient hotelServiceClient) {
        this.bookingRepository = bookingRepository;
        this.hotelServiceClient = hotelServiceClient;
    }

    //this runs at 12:30 everyday
    @Transactional
//    @Scheduled(cron = "0 30 0 * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void markNoShows() {

        LocalDate today = LocalDate.now();

        List<Booking> noShowBookings =
                bookingRepository.findNoShowCandidates(
                        BookingStatus.CONFIRMED,
                        today
                );

        for (Booking booking : noShowBookings) {	
            booking.setBookingStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            hotelServiceClient.updateRoomStatus(
                    booking.getHotelId(),
                    booking.getRoomId(),
                    "AVAILABLE"
            );
        }
    }
}
