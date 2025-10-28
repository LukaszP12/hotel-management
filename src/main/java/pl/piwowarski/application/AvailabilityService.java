package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.Room;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.repositories.BookingRepository;
import pl.piwowarski.repositories.RoomRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public AvailabilityService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<RoomAvailability> calendar(LocalDate start, LocalDate end, Long roomTypeId, int minCapacity) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Invalid date range: start must be before end");
        }

        List<Room> rooms = roomRepository.findFiltered(roomTypeId, minCapacity);
        if (rooms.isEmpty()) return List.of();

        List<Long> roomIds = rooms.stream().map(Room::getId).toList();
        List<Booking> bookings = bookingRepository.findOverlappingForRooms(roomIds, start, end);

        Map<Long, List<Booking>> bookingsByRoom =
                bookings.stream().collect(Collectors.groupingBy(b -> b.getRoom().getId()));

        List<LocalDate> days = start.datesUntil(end).toList();
        List<RoomAvailability> result = new ArrayList<>();

        for (Room room : rooms) {
            List<Booking> roomBookings = bookingsByRoom.getOrDefault(room.getId(), List.of());

            List<DayAvailability> dayAvailability = new ArrayList<>();
            for (LocalDate day : days) {
                boolean occupied = roomBookings.stream().anyMatch(b ->
                        !(b.getCheckOutDate().isEqual(day) || b.getCheckOutDate().isBefore(day)) &&
                                !(b.getCheckInDate().isAfter(day))
                );
                dayAvailability.add(new DayAvailability(day, occupied));
            }

            result.add(new RoomAvailability(room.getId(), room.getRoomName(), dayAvailability));
        }

        return result;
    }

    public List<Room> getAvailableRooms(LocalDate start, LocalDate end, Long roomTypeId, int minCapacity) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Invalid date range: start must be before end");
        }
        List<Room> rooms = roomRepository.findFiltered(roomTypeId, minCapacity);
        if (rooms.isEmpty()) {
            return List.of();
        }

        List<Long> roomIds = rooms.stream().map(Room::getId).toList();

        List<Booking> overlappingBookings = bookingRepository.findOverlappingForRooms(roomIds, start, end);

        Set<Long> bookedRoomsIds = overlappingBookings.stream()
                .map(b -> b.getRoom().getId())
                .collect(Collectors.toSet());

        return rooms.stream()
                .filter(room -> !bookedRoomsIds.contains(room.getId()))
                .toList();
    }

    // === DTOs for response ===

    public record DayAvailability(LocalDate date, boolean occupied) {
    }

    public record RoomAvailability(Long roomId, String roomName, List<DayAvailability> days) {
    }
}
