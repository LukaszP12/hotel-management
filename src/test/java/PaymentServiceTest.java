import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.application.BookingService;
import pl.piwowarski.application.PaymentService;
import pl.piwowarski.model.Guest;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.payment.Payment;
import pl.piwowarski.model.room.Room;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private BookingService bookingService;

    @Test
    public void shouldCreatePaymentForBooking() {
        // given
        Guest guest = new Guest("John", "Doe", "john.doe@hotmail.com");
        guest.setId(1L); // simulate existing guest

        Room room = new Room();
        room.setId(101L);
        room.setRoomName("Deluxe Room");
        room.setCapacity(2);

        Booking booking = new Booking(
                guest,
                room,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 5)
        );
        booking.setId(10L);
        Booking savedBooking = bookingService.createBooking(booking);

        Payment payment = new Payment();
        payment.setBooking(savedBooking);
        payment.setAmount(500.0);
        payment.setMethod("CARD");
        payment.setStatus("PAID");
        payment.setTransactionDate(LocalDateTime.now());

        // when
        Payment savedPayment = paymentService.createPayment(payment);

        // then
        assertNotNull(savedPayment.getId());
        assertEquals("PAID", savedPayment.getStatus());
        assertNotNull(savedPayment.getBooking());
    }

    @Test
    void shouldThrowWhenAmountIsZero() {
        Payment payment = new Payment();
        payment.setAmount(0);
        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(payment));
    }
}
