import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.piwowarski.application.AvailabilityService;
import pl.piwowarski.application.BookingService;
import pl.piwowarski.model.Room;
import pl.piwowarski.model.booking.Booking;

import org.springframework.beans.factory.annotation.Autowired;
import pl.piwowarski.model.booking.BookingStatus;
import pl.piwowarski.repositories.BookingRepository;
import pl.piwowarski.repositories.RoomRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking buildBooking(Long id, Long roomId, String in, String out, BookingStatus status) {
        Booking b = new Booking();
        b.setId(id);
        Room r = new Room();
        r.setId(roomId);
        b.setRoom(r);
        b.setCheckInDate(LocalDate.parse(in));
        b.setCheckOutDate(LocalDate.parse(out));
        b.setBookingStatus(status);
        return b;
    }

    private Room buildRoom(Long id, String name, int capacity) {
        Room r = new Room();
        r.setId(id);
        r.setRoomName(name);
        r.setCapacity(capacity);
        return r;
    }

    @Test
    void shouldCreateBooking() {
        Booking booking = new Booking("John Doe", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 5), "john.doe@hotmail.com");
        Booking saved = bookingService.createBooking(booking);
        assertNotNull(saved.getId());
    }

    @Test
    void shouldCancelBooking() {
        // given
        Booking booking = new Booking("John Doe",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 5),
                "john@example.com"
        );
        Booking saved = bookingService.createBooking(booking);

        // when
        bookingService.cancelBooking(saved.getId());
        Booking cancelled = bookingService.getBookingById(saved.getId());

        // then
        assertEquals(BookingStatus.CANCELLED, cancelled.getBookingStatus());
    }

    @Test
    void modifyBooking_shouldUpdateDates_whenSameRoomIsFree() {
        // given
        Booking existing = buildBooking(11L, 1L, "2025-01-10", "2025-01-13", BookingStatus.BOOKED);
        when(bookingRepository.findById(11L)).thenReturn(Optional.of(existing));

        Room newRoom = buildRoom(2L, "Room B", 2);
        // when
        when(roomRepository.findById(2L)).thenReturn(Optional.of(newRoom));

        when(bookingRepository.findOverlappingForRooms(eq(List.of(2L)), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.modifyBooking(11L, 2L,
                LocalDate.of(2025, 2, 2), LocalDate.of(2025, 2, 6));
        // then
        assertThat(result.getRoom().getId()).isEqualTo(2L);
        assertThat(result.getCheckInDate()).isEqualTo(LocalDate.of(2025, 2, 2));
        assertThat(result.getCheckOutDate()).isEqualTo(LocalDate.of(2025, 2, 6));
    }

    @Test
    void checkIn_shouldSetStatusToCheckedIn_whenValid() {
        // given
        Booking booking = buildBooking(1L, 1L, "2025-02-10", "2025-02-15", BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        // when
        Booking result = bookingService.checkIn(1L, LocalDate.of(2025, 2, 10));
        // then
        assertThat(result.getBookingStatus()).isEqualTo(BookingStatus.CHECKED_IN);
    }
}
