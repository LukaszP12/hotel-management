package pl.piwowarski.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piwowarski.model.booking.BookingNote;

import java.util.List;

public interface BookingNoteRepository extends JpaRepository<BookingNote, Long> {
    List<BookingNote> findAllByBookingIdOrderByCreatedAtDesc(Long bookingId);
}
