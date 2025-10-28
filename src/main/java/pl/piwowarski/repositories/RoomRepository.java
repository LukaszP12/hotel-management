package pl.piwowarski.repositories;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.piwowarski.model.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r")
    List<Room> findFiltered(@Param("roomTypeId") Long roomTypeId,@Param("minCapacity") int minCapacity);

    Optional<Room> findById(Long id);

    boolean existsById(Long id);
}
