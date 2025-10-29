import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.application.BookingService;
import pl.piwowarski.model.Guest;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.booking.BookingStatus;
import pl.piwowarski.repositories.BookingRepository;
import pl.piwowarski.repositories.GuestRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceHistoryTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private BookingService bookingService;

    private Guest buildGuest(Long id, String firstName) {
        Guest guest = new Guest();
        guest.setId(id);
        guest.setFirstName(firstName);
        return guest;
    }

    private Booking buildBooking(Long id, Long guestId, String checkIn, String checkOut, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setCheckInDate(LocalDate.parse(checkIn));
        booking.setCheckOutDate(LocalDate.parse(checkOut));
        booking.setBookingStatus(status);

        Guest guest = new Guest();
        guest.setId(guestId);
        booking.setGuest(guest);
        return booking;
    }

    @Test
    void shouldReturnAllBookingsForExistingGuest() {
        // given
        Long guestId = 1L;
        when(guestRepository.existsById(guestId)).thenReturn(true);
        when(bookingRepository.findAllByGuestId(guestId)).thenReturn(
                List.of(
                        buildBooking(1L, guestId, "2025-01-10", "2025-01-15", BookingStatus.BOOKED),
                        buildBooking(2L, guestId, "2024-12-01", "2024-12-05", BookingStatus.CHECKED_OUT)
                ));
        // when
        List<Booking> history = bookingService.getBookingHistoryForGuest(guestId);
        // then
        assertThat(history).hasSize(2);
    }

    @Test
    void shouldThrowExceptionWhenGuestNotFound() {
        // Arrange
        Long guestId = 999L;
        when(guestRepository.existsById(guestId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.getBookingHistoryForGuest(guestId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Guest not found");
    }
}
