package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.room.Room;
import pl.piwowarski.model.booking.Booking;
import pl.piwowarski.model.booking.BookingStatus;
import pl.piwowarski.model.room.RoomStatus;
import pl.piwowarski.repositories.BookingRepository;
import pl.piwowarski.repositories.GuestRepository;
import pl.piwowarski.repositories.RoomRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, GuestRepository guestRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
    }

    public Booking createBooking(Booking booking) {
        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id = " + booking.getRoom().getId()));

        if (room.getStatus() == RoomStatus.DIRTY || room.getStatus() == RoomStatus.IN_MAINTENANCE) {
            throw new IllegalStateException("Room is not available for booking");
        }

        booking.setRoom(room);
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

    public List<Booking> getBookingHistoryForGuest(Long guestId) {
        if (!guestRepository.existsById(guestId)) {
            throw new IllegalArgumentException("Guest not found with id: " + guestId);
        }
        return bookingRepository.findAllByGuestId(guestId);
    }

    public Booking checkOut(Long bookingId, LocalDate checkOutDate) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        if (booking.getBookingStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot check out a booking that is not checked in.");
        }

        if (checkOutDate.isBefore(booking.getCheckInDate())) {
            throw new IllegalStateException("Cannot check out before check-in date.");
        }

        booking.setBookingStatus(BookingStatus.CHECKED_OUT);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.DIRTY);
        roomRepository.save(room);

        return bookingRepository.save(booking);
    }
}
