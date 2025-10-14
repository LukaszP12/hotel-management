import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import pl.piwowarski.application.GuestService;
import pl.piwowarski.model.Guest;
import pl.piwowarski.repositories.GuestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GuestServiceTest {

    @Mock
    GuestRepository guestRepository;
    @InjectMocks
    GuestService guestService;

    @Test
    void createsGuest_whenEmailNotUsed() {
        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        Guest created = guestService.registerGuest(new Guest("John", "Doe", "john@example.com"));

        assertEquals("John", created.getFirstName());
        verify(guestRepository).save(any(Guest.class));
    }

    @Test
    void throwsException_whenEmailAlreadyExists() {
        Guest existing = new Guest();
        existing.setEmail("john@example.com");
        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                guestService.registerGuest(new Guest("John", "Doe", "john@example.com")));
    }
}
