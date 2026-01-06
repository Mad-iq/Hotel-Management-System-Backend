package com.hotel.booking;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.booking.client.AuthServiceClient;
import com.hotel.booking.client.HotelServiceClient;
import com.hotel.booking.client.dto.CategoryPricingDto;
import com.hotel.booking.client.dto.RoomDto;
import com.hotel.booking.client.dto.UserProfileDto;
import com.hotel.booking.entities.Booking;
import com.hotel.booking.entities.BookingStatus;
import com.hotel.booking.event.BookingEventPublisher;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.service.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelServiceClient hotelServiceClient;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private BookingEventPublisher bookingEventPublisher;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private RoomDto room;
    private CategoryPricingDto pricing;

    @BeforeEach
    void setup() {
        room = new RoomDto();
        room.setId(1L);
        room.setCategoryId(10L);
        room.setStatus("AVAILABLE");

        pricing = new CategoryPricingDto();
        pricing.setBasePrice(BigDecimal.valueOf(1000));
        pricing.setCurrency("INR");
    }

    @Test
    void createBooking_success() {
        UserProfileDto userProfile = new UserProfileDto();
        userProfile.setEmail("test@test.com");

        when(authServiceClient.getProfile(anyString())).thenReturn(userProfile);
        when(hotelServiceClient.getRoomsByHotel(1L)).thenReturn(List.of(room));
        when(bookingRepository.findOverlappingBookings(
                anyLong(), any(), any(), any()))
                .thenReturn(List.of());
        when(hotelServiceClient.getBasePricing(10L)).thenReturn(pricing);
        when(hotelServiceClient.getSeasonalPricing(anyLong(), any()))
                .thenReturn(List.of());
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking booking = bookingService.createBooking(
                1L,
                "Bearer token",
                1L,
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        assertNotNull(booking);
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
        verify(bookingEventPublisher, times(1)).publish(any());
    }

    @Test
    void createBooking_roomAlreadyBooked_shouldThrowException() {
        when(authServiceClient.getProfile(anyString()))
                .thenReturn(new UserProfileDto());
        when(hotelServiceClient.getRoomsByHotel(1L))
                .thenReturn(List.of(room));
        when(bookingRepository.findOverlappingBookings(
                anyLong(), any(), any(), any()))
                .thenReturn(List.of(new Booking()));

        assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking(
                        1L,
                        "Bearer token",
                        1L,
                        1L,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                )
        );
    }

    @Test
    void getBookingById_notFound_shouldThrowException() {
        when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getBookingById(99L)
        );
    }

    @Test
    void cancelBooking_success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(1L);
        booking.setHotelId(1L);
        booking.setRoomId(1L);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        UserProfileDto userProfile = new UserProfileDto();
        userProfile.setEmail("user@test.com");

        when(authServiceClient.getProfile(anyString()))
                .thenReturn(userProfile);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        Booking cancelled = bookingService.cancelBooking(1L, 1L, "Bearer token");

        assertEquals(BookingStatus.CANCELLED, cancelled.getBookingStatus());
        verify(hotelServiceClient)
                .updateRoomStatus(1L, 1L, "AVAILABLE");
    }

    @Test
    void checkIn_wrongDate_shouldThrowException() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setCheckInDate(LocalDate.now().plusDays(1));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () ->
                bookingService.checkIn(1L)
        );
    }
    
    @Test
    void createBooking_roomUnderMaintenance_shouldThrowException() {
        room.setStatus("MAINTENANCE");

        when(authServiceClient.getProfile(anyString()))
                .thenReturn(new UserProfileDto());
        when(hotelServiceClient.getRoomsByHotel(1L))
                .thenReturn(List.of(room));

        assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking(
                        1L,
                        "Bearer token",
                        1L,
                        1L,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                )
        );
    }
    
    @Test
    void cancelBooking_wrongUser_shouldThrowException() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(99L); // different user

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(authServiceClient.getProfile(anyString()))
                .thenReturn(new UserProfileDto());

        assertThrows(IllegalStateException.class, () ->
                bookingService.cancelBooking(1L, 1L, "Bearer token")
        );
    }

    @Test
    void checkIn_success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setHotelId(1L);
        booking.setRoomId(1L);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setCheckInDate(LocalDate.now());

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        Booking checkedIn = bookingService.checkIn(1L);

        assertEquals(BookingStatus.CHECKED_IN, checkedIn.getBookingStatus());
        verify(hotelServiceClient)
                .updateRoomStatus(1L, 1L, "OCCUPIED");
    }

    @Test
    void checkOut_notCheckedIn_shouldThrowException() {
        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () ->
                bookingService.checkOut(1L)
        );
    }


}
