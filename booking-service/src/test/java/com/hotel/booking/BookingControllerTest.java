package com.hotel.booking;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.booking.controller.BookingController;
import com.hotel.booking.controller.dto.AvailableHotelDto;
import com.hotel.booking.controller.dto.AvailableRoomsByCategoryDto;
import com.hotel.booking.controller.dto.CreateBookingRequest;
import com.hotel.booking.controller.dto.SearchHotelsRequest;
import com.hotel.booking.entities.Booking;
import com.hotel.booking.entities.BookingStatus;
import com.hotel.booking.service.BookingService;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void createBooking_success() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setHotelId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingService.createBooking(
                1L,
                "Bearer token",
                1L,
                1L,
                request.getCheckIn(),
                request.getCheckOut()
        )).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                .header("X-USER-ID", "1")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    void getBookingById_success() throws Exception {
        Booking booking = new Booking();
        booking.setId(5L);

        when(bookingService.getBookingById(5L)).thenReturn(booking);

        mockMvc.perform(get("/api/bookings/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }


    @Test
    void cancelBooking_success() throws Exception {
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setBookingStatus(BookingStatus.CANCELLED);

        when(bookingService.cancelBooking(2L, 1L, "Bearer token"))
                .thenReturn(booking);

        mockMvc.perform(delete("/api/bookings/2")
                .header("X-USER-ID", "1")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("CANCELLED"));
    }


    @Test
    void getUserBookings_success() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingService.getBookingsByUser(1L))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/api/bookings/user/mine")
                .header("X-USER-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }


    @Test
    void getAllBookings_success() throws Exception {
        Booking booking = new Booking();
        booking.setId(10L);

        when(bookingService.getAllBookings())
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void searchAvailableHotels_success() throws Exception {
        SearchHotelsRequest request = new SearchHotelsRequest();
        request.setCity("Bangalore");
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(2));
        request.setGuests(2);

        AvailableHotelDto dto = new AvailableHotelDto();
        dto.setHotelId(1L);
        dto.setCity("Bangalore");

        when(bookingService.searchAvailableHotels(
                request.getCity(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getGuests()
        )).thenReturn(List.of(dto));

        mockMvc.perform(post("/api/bookings/search/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hotelId").value(1));
    }

    @Test
    void checkIn_success() throws Exception {
        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.CHECKED_IN);

        when(bookingService.checkIn(3L)).thenReturn(booking);

        mockMvc.perform(post("/api/bookings/3/check-in"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("CHECKED_IN"));
    }


    @Test
    void checkOut_success() throws Exception {
        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.CHECKED_OUT);

        when(bookingService.checkOut(3L)).thenReturn(booking);

        mockMvc.perform(post("/api/bookings/3/check-out"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value("CHECKED_OUT"));
    }


    @Test
    void getAvailableRoomsByCategory_success() throws Exception {
        AvailableRoomsByCategoryDto dto = new AvailableRoomsByCategoryDto();
        dto.setCategoryId(1L);
        dto.setPricePerNight(BigDecimal.valueOf(1200));

        when(bookingService.getAvailableRoomsByCategory(
                1L,
                LocalDate.parse("2026-01-10"),
                LocalDate.parse("2026-01-12"),
                2
        )).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bookings/available-rooms")
                .param("hotelId", "1")
                .param("checkIn", "2026-01-10")
                .param("checkOut", "2026-01-12")
                .param("guests", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1));
    }
}
