
import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Test
    void shouldCreatePaymentForBooking() {
        // given
        Booking booking = new Booking("John Doe", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 5));
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
}
