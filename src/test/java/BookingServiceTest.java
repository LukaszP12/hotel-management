import java.time.LocalDate;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    @Test
    void shouldCreateBooking() {
        Booking booking = new Booking(null, "John Doe", LocalDate.now(), LocalDate.now().plusDays(2))
        Booking saved = bookingService.createBooking(booking);
        assertNotNull(saved.getId());
    }
}

