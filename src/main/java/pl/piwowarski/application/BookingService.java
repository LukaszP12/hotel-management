package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.booking.BookingStatus;
import pl.piwowarski.repositories.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    public Booking cancelBooking(Long id) {
        Booking bookingById = getBookingById(id);

        if (bookingById.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled.");
        }

        bookingById.setBookingStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(bookingById);
    }
}
