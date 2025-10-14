package pl.piwowarski.application;

import pl.piwowarski.model.Guest;
import pl.piwowarski.repositories.GuestRepository;

import java.util.List;
import java.util.Optional;

@Service
class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public Guest saveGuest(Guest guest) {
        return guestRepository.save(guest);
    }

    @Override
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    @Override
    public Optional<Guest> getGuestById(Long id) {
        return guestRepository.findById(id);
    }

    @Override
    public Guest updateGuest(Long id, Guest updatedGuest) {
        return guestRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(updatedGuest.getFirstName());
                    existing.setLastName(updatedGuest.getLastName());
                    existing.setEmail(updatedGuest.getEmail());
                    existing.setPhoneNumber(updatedGuest.getPhoneNumber());
                    existing.setDateOfBirth(updatedGuest.getDateOfBirth());
                    return guestRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + id));
    }

    @Override
    public void deleteGuest(Long id) {
        if (!guestRepository.existsById(id)) {
            throw new RuntimeException("Guest not found with id: " + id);
        }
        guestRepository.deleteById(id);
    }
}
