package com.hotel.hotels;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.hotels.controller.HotelController;
import com.hotel.hotels.dto.HotelRequestDto;
import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.service.HotelService;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void createHotel_success() throws Exception {
        HotelRequestDto request = new HotelRequestDto();
        request.setName("Test Hotel");
        request.setCity("Bangalore");
        request.setState("KA");
        request.setCountry("India");
        request.setStarRating(4);

        Hotel saved = new Hotel();
        saved.setId(1L);
        saved.setName("Test Hotel");
        saved.setCity("Bangalore");
        saved.setState("KA");
        saved.setCountry("India");
        saved.setStarRating(4);

        when(hotelService.createHotel(any(Hotel.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Hotel"))
                .andExpect(jsonPath("$.city").value("Bangalore"));
    }

    @Test
    void getAllHotels_success() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setId(2L);
        hotel.setName("City Inn");

        when(hotelService.getAllActiveHotels())
                .thenReturn(List.of(hotel));

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("City Inn"));
    }
}
