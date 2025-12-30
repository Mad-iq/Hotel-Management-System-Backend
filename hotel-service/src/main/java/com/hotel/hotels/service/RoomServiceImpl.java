package com.hotel.hotels.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.entity.Room;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.entity.RoomStatus;
import com.hotel.hotels.repository.HotelRepository;
import com.hotel.hotels.repository.RoomCategoryRepository;
import com.hotel.hotels.repository.RoomRepository;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomCategoryRepository categoryRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
                           HotelRepository hotelRepository,
                           RoomCategoryRepository categoryRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Room addRoom(Long hotelId, Long categoryId, Room room) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        RoomCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        roomRepository.findByHotelIdAndRoomNumberAndActiveTrue(
                hotelId, room.getRoomNumber()
        ).ifPresent(r -> {
            throw new RuntimeException("Room number already exists in this hotel");
        });

        room.setId(null);
        room.setHotel(hotel);
        room.setCategory(category);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setActive(true);

        return roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndActiveTrue(hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByHotelAndStatus(Long hotelId, RoomStatus status) {
        return roomRepository.findByHotelIdAndStatusAndActiveTrue(hotelId, status);
    }

    @Override
    public Room updateRoomStatus(Long roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(status);
        return roomRepository.save(room);
    }
}
