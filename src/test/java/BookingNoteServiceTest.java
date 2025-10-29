import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.repositories.BookingRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingNoteServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingNoteRepository bookingNoteRepository;

    @InjectMocks
    private BookingNoteService bookingNoteService;

    @Test
    void shouldAddNoteToBooking() {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingNoteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingNote result = bookingNoteService.addNote(1L, "Check mini bar", "Alice");

        assertThat(result.getText()).isEqualTo("Check mini bar");
        assertThat(result.getAuthor()).isEqualTo("Alice");
        assertThat(result.getBooking().getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenNoteTextIsEmpty() {
        assertThatThrownBy(() -> bookingNoteService.addNote(1L, "  ", "Mike"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Note text cannot be empty");
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingNoteService.addNote(99L, "Hello", "Staff"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }
}
