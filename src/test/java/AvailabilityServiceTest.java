import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.Room;
import pl.piwowarski.model.booking.BookingStatus;
import pl.piwowarski.repositories.BookingRepository;
import pl.piwowarski.repositories.RoomRepository;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvailabilityServiceTest {

    private AvailabilityService availabilityService;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;

    private final LocalDate start = LocalDate.of(2025, 1, 10);
    private final LocalDate end = LocalDate.of(2025, 1, 15);

    @BeforeEach
    void setup() {
        roomRepository = mock(RoomRepository.class);
        bookingRepository = mock(BookingRepository.class);
        availabilityService = new AvailabilityService(roomRepository, bookingRepository);
    }

    private Room buildRoom(Long id, String name) {
        Room r = new Room();
        r.setId(id);
        r.setName(name);
        return r;
    }

    private Booking buildBooking(Long roomId, String start, String end) {
        Booking b = new Booking();
        Room r = new Room();
        r.setId(roomId);
        b.setRoom(r);
        b.setCheckInDate(LocalDate.parse(start));
        b.setCheckOutDate(LocalDate.parse(end));
        b.setBookingStatus(BookingStatus.CONFIRMED);
        return b;
    }

    @Test
    void shouldReturnEmptyListWhenNoRooms() {
        when(roomRepository.findFiltered(null, 1)).thenReturn(List.of());
        var result = availabilityService.calendar(start, end, null, 1);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldMarkAllDaysFreeWhenNoBookings() {
        var rooms = List.of(buildRoom(1L, "Room A"));
        when(roomRepository.findFiltered(null, 1)).thenReturn(rooms);
        when(bookingRepository.findOverlappingForRooms(any(), any(), any()))
                .thenReturn(List.of());

        var result = availabilityService.calendar(start, end, null, 1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).days())
                .allMatch(day -> !day.occupied());
    }

    @Test
    void shouldMarkOccupiedDaysCorrectly() {
        var rooms = List.of(buildRoom(1L, "Room A"));
        when(roomRepository.findFiltered(null, 1)).thenReturn(rooms);
        when(bookingRepository.findOverlappingForRooms(any(), any(), any()))
                .thenReturn(List.of(
                        buildBooking(1L, "2025-01-11", "2025-01-13")
                ));

        var result = availabilityService.calendar(start, end, null, 1);
        var days = result.get(0).days();

        assertThat(days).extracting("occupied")
                .containsExactly(false, true, true, false, false);
    }

    @Test
    void shouldIgnoreCancelledBookings() {
        var rooms = List.of(buildRoom(1L, "Room A"));

        Booking cancelled = buildBooking(1L, "2025-01-11", "2025-01-13");
        cancelled.setStatus(BookingStatus.CANCELLED);

        when(roomRepository.findFiltered(null, 1)).thenReturn(rooms);
        when(bookingRepository.findOverlappingForRooms(any(), any(), any()))
                .thenReturn(List.of(cancelled));

        var result = availabilityService.calendar(start, end, null, 1);
        assertThat(result.get(0).days())
                .allMatch(day -> !day.occupied());
    }

    @Test
    void shouldThrowExceptionIfInvalidDates() {
        var invalidStart = LocalDate.of(2025, 1, 20);
        var invalidEnd = LocalDate.of(2025, 1, 10);

        assertThatThrownBy(() ->
                availabilityService.calendar(invalidStart, invalidEnd, null, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
