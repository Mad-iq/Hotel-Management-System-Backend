package com.hotel.hotels.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.hotels.entity.Room;
import com.hotel.hotels.entity.RoomStatus;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelIdAndActiveTrue(Long hotelId);

    List<Room> findByHotelIdAndStatusAndActiveTrue(
            Long hotelId,
            RoomStatus status
    );

    Optional<Room> findByHotelIdAndRoomNumberAndActiveTrue(
            Long hotelId,
            String roomNumber
    );
}
