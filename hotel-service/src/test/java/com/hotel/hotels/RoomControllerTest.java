package com.hotel.hotels;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.hotel.hotels.controller.RoomController;
import com.hotel.hotels.dto.RoomRequestDto;
import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.entity.Room;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.entity.RoomStatus;
import com.hotel.hotels.service.RoomService;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void addRoom_success() throws Exception {
        RoomRequestDto request = new RoomRequestDto();
        request.setRoomNumber("101");

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        RoomCategory category = new RoomCategory();
        category.setId(2L);

        Room room = new Room();
        room.setId(10L);
        room.setRoomNumber("101");
        room.setHotel(hotel);
        room.setCategory(category);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomService.addRoom(eq(1L), eq(2L), any(Room.class)))
                .thenReturn(room);

        mockMvc.perform(post("/api/hotels/1/rooms")
                .param("categoryId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.roomNumber").value("101"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void listRooms_success() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        RoomCategory category = new RoomCategory();
        category.setId(2L);

        Room room = new Room();
        room.setId(20L);
        room.setRoomNumber("102");
        room.setHotel(hotel);
        room.setCategory(category);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomService.getRoomsByHotel(1L))
                .thenReturn(List.of(room));

        mockMvc.perform(get("/api/hotels/1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].roomNumber").value("102"));
    }


    @Test
    void updateRoomStatus_success() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        RoomCategory category = new RoomCategory();
        category.setId(2L);

        Room room = new Room();
        room.setId(30L);
        room.setHotel(hotel);
        room.setCategory(category);
        room.setRoomNumber("103");
        room.setStatus(RoomStatus.MAINTENANCE);

        when(roomService.updateRoomStatus(30L, RoomStatus.MAINTENANCE))
                .thenReturn(room);

        mockMvc.perform(put("/api/hotels/1/rooms/30/status")
                .param("status", "MAINTENANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));
    }
}
