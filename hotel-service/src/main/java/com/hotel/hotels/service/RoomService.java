package com.hotel.hotels.service;

import java.util.List;

import com.hotel.hotels.entity.Room;
import com.hotel.hotels.entity.RoomStatus;

public interface RoomService {

    Room addRoom(Long hotelId, Long categoryId, Room room);

    List<Room> getRoomsByHotel(Long hotelId);

    List<Room> getRoomsByHotelAndStatus(Long hotelId, RoomStatus status);

    Room updateRoomStatus(Long roomId, RoomStatus status);
}
