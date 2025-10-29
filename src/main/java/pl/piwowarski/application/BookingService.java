package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.Room;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.booking.BookingStatus;
import pl.piwowarski.repositories.BookingRepository;
import pl.piwowarski.repositories.RoomRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
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

    public Booking modifyBooking(Long bookingId,
                                 Long newRoomId,
                                 LocalDate newCheckIn,
                                 LocalDate newCheckOut) {
        if (newCheckIn == null || newCheckOut == null || !newCheckIn.isBefore(newCheckOut)) {
            throw new IllegalArgumentException("Invalid date range: start must be before end");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id = " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify a cancelled booking");
        }

        Room targetRoom = booking.getRoom();
        if (newRoomId != null && !newRoomId.equals(booking.getRoom().getId())) {
            targetRoom = roomRepository.findById(newRoomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found with id = " + newRoomId));
        }

        var overlaps = bookingRepository.findOverlappingForRooms(
                List.of(targetRoom.getId()), newCheckIn, newCheckOut
        );

        boolean conflict = overlaps.stream()
                .filter(b -> !b.getId().equals(bookingId))
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .findAny()
                .isPresent();

        if (conflict) {
            throw new IllegalStateException("Target room is not available for the selected dates");
        }

        booking.setRoom(targetRoom);
        booking.setCheckInDate(newCheckIn);
        booking.setCheckOutDate(newCheckOut);
        return bookingRepository.save(booking);
    }

    public Booking checkIn(Long bookingId, LocalDate today) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot check in a cancelled booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Guest is already checked in.");
        }

        if (today.isBefore(booking.getCheckInDate())) {
            throw new IllegalStateException("Cannot check in before the check-in date.");
        }

        booking.setBookingStatus(BookingStatus.CHECKED_IN);
        return bookingRepository.save(booking);
    }
}
