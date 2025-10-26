package pl.piwowarski.repositories;

import pl.piwowarski.model.Guest;

import java.util.List;
import java.util.Optional;

public interface GuestRepository {

    Optional<Guest> findById(Long id);

    List<Guest> findAll();

    Optional<Guest> findByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    void deleteById(Long id);

    Guest save(Guest guest);

}
