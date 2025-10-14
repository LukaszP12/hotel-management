import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.piwowarski.model.Booking;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    @Test
    void shouldCreateBooking() {
        Booking booking = new Booking("John Doe",LocalDate.of(2026, 4, 1),LocalDate.of(2026, 4, 5),"john.doe@hotmail.com");
        Booking saved = bookingService.createBooking(booking);
        assertNotNull(saved.getId());
    }
}

