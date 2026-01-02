package com.hotel.booking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.booking.client.HotelServiceClient;
import com.hotel.booking.client.dto.CategoryPricingDto;
import com.hotel.booking.client.dto.HotelDto;
import com.hotel.booking.client.dto.RoomDto;
import com.hotel.booking.client.dto.SeasonalPricingDto;
import com.hotel.booking.controller.dto.AvailableHotelDto;
import com.hotel.booking.entities.Booking;
import com.hotel.booking.entities.BookingStatus;
import com.hotel.booking.event.BookingEventPublisher;
import com.hotel.booking.event.dto.BookingCreatedEvent;
import com.hotel.booking.repository.BookingRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelServiceClient hotelServiceClient;
    private final BookingEventPublisher bookingEventPublisher;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            HotelServiceClient hotelServiceClient,  BookingEventPublisher bookingEventPublisher){
        this.bookingRepository = bookingRepository;
        this.hotelServiceClient = hotelServiceClient;
        this.bookingEventPublisher = bookingEventPublisher;
    }

    @Override
    public Booking createBooking(Long userId,Long hotelId,Long roomId,LocalDate checkIn, LocalDate checkOut){
        List<RoomDto> rooms = hotelServiceClient.getRoomsByHotel(hotelId);
        RoomDto room = rooms.stream()
                .filter(r -> r.getId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Room not found in this hotel"));

        if ("MAINTENANCE".equals(room.getStatus())) {
            throw new IllegalStateException("Room is under maintenance");
        }
        boolean hasOverlap = !bookingRepository.findOverlappingBookings(roomId,checkIn,checkOut,List.of(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN)).isEmpty();
        if (hasOverlap){
            throw new IllegalStateException("Room is already booked for the given dates");
        }

        BigDecimal totalAmount = calculatePrice(room.getCategoryId(),checkIn,checkOut);

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setHotelId(hotelId);
        booking.setRoomId(roomId);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);
        //hotelServiceClient.updateRoomStatus(hotelId, roomId, "OCCUPIED");
        
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setBookingId(savedBooking.getId());
        event.setUserId(savedBooking.getUserId());
        event.setHotelId(savedBooking.getHotelId());
        event.setRoomId(savedBooking.getRoomId());
        event.setCheckInDate(savedBooking.getCheckInDate());
        event.setCheckOutDate(savedBooking.getCheckOutDate());
        event.setTotalAmount(savedBooking.getTotalAmount());
        event.setCreatedAt(savedBooking.getCreatedAt());

        try {
            bookingEventPublisher.publishBookingCreated(event);
        } catch (Exception e) {
            log.error("Booking {} created but Kafka publish failed",savedBooking.getId(),e);
        }
        return savedBooking;
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    @Override
    public Booking cancelBooking(Long bookingId, Long userId) {

        Booking booking = getBookingById(bookingId);

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalStateException("You cannot cancel this booking");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        bookingRepository.save(booking);

        hotelServiceClient.updateRoomStatus(
                booking.getHotelId(),
                booking.getRoomId(),
                "AVAILABLE"
        );
        return savedBooking;
    }

    private BigDecimal calculatePrice(
            Long categoryId,
            LocalDate checkIn,
            LocalDate checkOut) {

        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();

        if (nights <= 0) {
            throw new IllegalArgumentException("Invalid booking dates");
        }

        CategoryPricingDto basePricing =
                hotelServiceClient.getBasePricing(categoryId);

        BigDecimal pricePerNight = basePricing.getBasePrice();

        // Optional seasonal override
        List<SeasonalPricingDto> seasonalPrices =hotelServiceClient.getSeasonalPricing(categoryId, checkIn);
        if (!seasonalPrices.isEmpty()){
            pricePerNight = seasonalPrices.get(0).getPrice();
        }

        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }
    
    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    //search logic
    @Override
    public List<AvailableHotelDto> searchAvailableHotels(String city,LocalDate checkIn,LocalDate checkOut,Integer guests){
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in must be before check-out");
        }

        List<HotelDto> hotels = hotelServiceClient.getAllHotels();
        return hotels.stream().filter(h -> h.getCity().equalsIgnoreCase(city))
                .map(hotel -> {
                    List<RoomDto> rooms =hotelServiceClient.getRoomsByHotel(hotel.getId());
                    List<RoomDto> availableRoomList = rooms.stream()
                            .filter(room -> !"MAINTENANCE".equals(room.getStatus()))
                            .filter(room -> {
                                boolean hasOverlap =!bookingRepository.findOverlappingBookings(room.getId(),checkIn,checkOut,List.of(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN)).isEmpty();
                               return !hasOverlap;
                            }).toList();

                    int availableRooms = availableRoomList.size();

                    if (availableRooms > 0){
                        BigDecimal startingFromPrice = availableRoomList.stream().map(room ->
                             calculatePrice(room.getCategoryId(),checkIn,checkOut))
                            .min(BigDecimal::compareTo)
                            .orElse(null);
                        AvailableHotelDto dto = new AvailableHotelDto();
                         dto.setHotelId(hotel.getId());
                         dto.setName(hotel.getName());
                        dto.setCity(hotel.getCity());
                        dto.setStarRating(hotel.getStarRating());
                        dto.setAvailableRooms((int) availableRooms);
                        dto.setStartingFromPrice(startingFromPrice);
                        return dto;}
                    return null;
                    }).filter(dto -> dto != null).toList();
        }
    
    @Override
    public Booking checkIn(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be checked in");
        }

        if (!LocalDate.now().isEqual(booking.getCheckInDate())) {
            throw new IllegalStateException("Check-in allowed only on check-in date");
        }

        booking.setBookingStatus(BookingStatus.CHECKED_IN);
        Booking savedBooking = bookingRepository.save(booking);
        hotelServiceClient.updateRoomStatus(booking.getHotelId(),booking.getRoomId(),"OCCUPIED");
        return savedBooking;
    }
    
    @Override
    public Booking checkOut(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBookingStatus() != BookingStatus.CHECKED_IN){
            throw new IllegalStateException("Only checked-in bookings can be checked out");
        }

        booking.setBookingStatus(BookingStatus.CHECKED_OUT);
        Booking savedBooking = bookingRepository.save(booking);
        hotelServiceClient.updateRoomStatus( booking.getHotelId(),booking.getRoomId(),"AVAILABLE");
        return savedBooking;
    }



}
