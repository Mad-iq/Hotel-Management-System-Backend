package com.hotel.hotels;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.entity.Room;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.entity.RoomStatus;
import com.hotel.hotels.repository.HotelRepository;
import com.hotel.hotels.repository.RoomCategoryRepository;
import com.hotel.hotels.repository.RoomRepository;
import com.hotel.hotels.service.RoomServiceImpl;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomCategoryRepository categoryRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    void addRoom_success() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        RoomCategory category = new RoomCategory();
        category.setId(2L);

        Room room = new Room();
        room.setRoomNumber("101");

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));
        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));
        when(roomRepository.findByHotelIdAndRoomNumberAndActiveTrue(1L, "101"))
                .thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class)))
                .thenAnswer(i -> i.getArgument(0));

        Room saved = roomService.addRoom(1L, 2L, room);

        assertEquals(RoomStatus.AVAILABLE, saved.getStatus());
        assertEquals(hotel, saved.getHotel());
        assertEquals(category, saved.getCategory());
        verify(roomRepository).save(saved);
    }

    @Test
    void addRoom_duplicateRoomNumber_shouldThrowException() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        RoomCategory category = new RoomCategory();
        category.setId(2L);

        Room room = new Room();
        room.setRoomNumber("101");

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));
        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));
        when(roomRepository.findByHotelIdAndRoomNumberAndActiveTrue(1L, "101"))
                .thenReturn(Optional.of(new Room()));

        assertThrows(RuntimeException.class, () ->
                roomService.addRoom(1L, 2L, room)
        );
    }

    @Test
    void updateRoomStatus_success() {
        Room room = new Room();
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.findById(5L))
                .thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class)))
                .thenAnswer(i -> i.getArgument(0));

        Room updated = roomService.updateRoomStatus(5L, RoomStatus.MAINTENANCE);

        assertEquals(RoomStatus.MAINTENANCE, updated.getStatus());
        verify(roomRepository).save(updated);
    }
}
