package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.booking.BookingNote;
import pl.piwowarski.repositories.BookingNoteRepository;
import pl.piwowarski.repositories.BookingRepository;

@Service
public class BookingNoteService {

    private final BookingRepository bookingRepository;
    private final BookingNoteRepository bookingNoteRepository;

    public BookingNoteService(BookingRepository bookingRepository, BookingNoteRepository bookingNoteRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingNoteRepository = bookingNoteRepository;
    }

    public BookingNote addNote(Long bookingId, String text, String author) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Note text cannot be empty");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        BookingNote note = new BookingNote(booking, text.trim(), author == null ? "Unknown" : author);
        return bookingNoteRepository.save(note);
    }
}
