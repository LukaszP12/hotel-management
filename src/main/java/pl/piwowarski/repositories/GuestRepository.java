package pl.piwowarski.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.piwowarski.model.Guest;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest,Long> {

    Optional<Guest> findById(Long id);

    List<Guest> findAll();

    Optional<Guest> findByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    void deleteById(Long id);

    Guest save(Guest guest);

}
