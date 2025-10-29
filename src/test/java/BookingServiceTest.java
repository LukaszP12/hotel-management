import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.piwowarski.application.BookingService;
import pl.piwowarski.model.booking.Booking;

import org.springframework.beans.factory.annotation.Autowired;
import pl.piwowarski.model.booking.BookingStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    BookingService bookingService;

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
        assertEquals(BookingStatus.CANCELLED,cancelled.getBookingStatus());
    }
}
