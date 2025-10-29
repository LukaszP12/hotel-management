import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.application.BookingNoteService;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.booking.BookingNote;
import pl.piwowarski.repositories.BookingNoteRepository;
import pl.piwowarski.repositories.BookingRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @Test
    void shouldReturnAllNotesForBooking() {
        Booking booking = new Booking();
        booking.setId(1L);

        BookingNote note1 = new BookingNote(booking, "Mini bar empty", "Alice");
        note1.setId(100L);
        BookingNote note2 = new BookingNote(booking, "Guest asked for quiet room", "Bob");
        note2.setId(101L);

        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingNoteRepository.findAllByBookingIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(note2, note1)); // latest first

        List<BookingNote> result = bookingNoteService.getNotesForBooking(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAuthor()).isEqualTo("Bob"); // newest first
    }

    @Test
    void shouldThrowExceptionWhenGettingNotesForUnknownBooking() {
        when(bookingRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookingNoteService.getNotesForBooking(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }
}
