package com.hotel.hotels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.hotels.entity.Hotel;
import com.hotel.hotels.repository.HotelRepository;
import com.hotel.hotels.service.HotelServiceImpl;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Test
    void createHotel_shouldSetDefaultsAndSave() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");

        when(hotelRepository.save(any(Hotel.class)))
                .thenAnswer(i -> i.getArgument(0));

        Hotel saved = hotelService.createHotel(hotel);

        assertNull(saved.getId());   
        verify(hotelRepository).save(saved);
    }

    @Test
    void getAllActiveHotels_success() {
        Hotel hotel = new Hotel();
        hotel.setActive(true);

        when(hotelRepository.findByActiveTrue())
                .thenReturn(List.of(hotel));

        List<Hotel> result = hotelService.getAllActiveHotels();

        assertEquals(1, result.size());
        verify(hotelRepository).findByActiveTrue();
    }

    @Test
    void getHotelById_notFound_shouldThrowException() {
        when(hotelRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                hotelService.getHotelById(99L)
        );
    }

    @Test
    void deactivateHotel_shouldSetInactive() {
        Hotel hotel = new Hotel();
        hotel.setActive(true);

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        hotelService.deactivateHotel(1L);
        verify(hotelRepository).save(hotel);
    }
}
