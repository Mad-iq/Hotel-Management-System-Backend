package com.hotel.hotels.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.hotel.hotels.dto.RoomRequestDto;
import com.hotel.hotels.dto.RoomResponseDto;
import com.hotel.hotels.entity.Room;
import com.hotel.hotels.entity.RoomStatus;
import com.hotel.hotels.service.RoomService;

@RestController
@RequestMapping("/api/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public RoomResponseDto addRoom(
            @PathVariable Long hotelId,
            @RequestParam Long categoryId,
            @RequestBody RoomRequestDto dto) {

        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());

        Room saved = roomService.addRoom(hotelId, categoryId, room);
        return map(saved);
    }

    @GetMapping
    public List<RoomResponseDto> listRooms(@PathVariable Long hotelId) {
        return roomService.getRoomsByHotel(hotelId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @PutMapping("/{roomId}/status")
    public RoomResponseDto updateStatus(
            @PathVariable Long roomId,
            @RequestParam RoomStatus status) {

        return map(roomService.updateRoomStatus(roomId, status));
    }

    private RoomResponseDto map(Room r) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setId(r.getId());
        dto.setRoomNumber(r.getRoomNumber());
        dto.setStatus(r.getStatus());
        return dto;
    }
}
