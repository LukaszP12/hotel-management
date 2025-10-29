package pl.piwowarski.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.piwowarski.model.booking.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>{

    Booking save(Booking booking);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id IN :roomIds
          AND b.checkInDate < :end
          AND b.checkOutDate > :start
    """)
    List<Booking> findOverlappingForRooms(
            @Param("roomIds") List<Long> roomIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    Optional<Booking> findById();

    List<Booking> findAllByGuestId(Long guestId);
}
