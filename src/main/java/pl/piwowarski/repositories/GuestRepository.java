package pl.piwowarski.repositories;

import pl.piwowarski.model.Guest;

public interface GuestRepository {

    Guest findByEmail(String email);

    boolean existsById(Long id);

    void deleteById(Long id);
}
