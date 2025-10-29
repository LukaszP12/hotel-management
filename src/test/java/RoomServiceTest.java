import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.application.RoomService;
import pl.piwowarski.model.room.Room;
import pl.piwowarski.model.room.RoomStatus;
import pl.piwowarski.model.room.RoomType;
import pl.piwowarski.repositories.RoomRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService; // This doesn't exist yet — TDD step 2!

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 🧩 Test #1: Save a new room
    @Test
    void shouldSaveRoomSuccessfully() {
        Room room = new Room(RoomType.Single, 120.0, true);
        Room savedRoom = new Room(RoomType.Single, 120.0, true);

        when(roomRepository.save(room)).thenReturn(savedRoom);

        Room result = roomService.saveRoom(room);

        assertNotNull(result.getId());
        assertEquals("101", result.getRoomNumber());
        verify(roomRepository, times(1)).save(room);
    }

    // 🧩 Test #2: Get all rooms
    @Test
    void shouldReturnAllRooms() {
        List<Room> mockRooms = List.of(
                new Room(RoomType.Single, 100.0, true),
                new Room(RoomType.Double, 150.0, true)
        );

        when(roomRepository.findAll()).thenReturn(mockRooms);

        List<Room> rooms = roomService.getAllRooms();

        assertEquals(2, rooms.size());
        assertEquals("101", rooms.get(0).getRoomNumber());
        verify(roomRepository, times(1)).findAll();
    }

    // 🧩 Test #3: Find room by ID
    @Test
    void shouldReturnRoomById() {
        Room room = new Room(RoomType.Single, 120.0, true);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Room foundRoom = roomService.getRoomById(1L);

        assertNotNull(foundRoom);
        assertEquals("101", foundRoom.getRoomNumber());
        verify(roomRepository, times(1)).findById(1L);
    }

    // 🧩 Test #4: Update existing room
    @Test
    void shouldUpdateRoomSuccessfully() {
        Room existingRoom = new Room(RoomType.Single, 100.0, true);
        Room updatedRoom = new Room(RoomType.Suite, 180.0, false);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Room result = roomService.updateRoom(1L, updatedRoom);

        assertEquals("Suite", result.getType());
        assertEquals(180.0, result.getPrice());
        assertFalse(result.isAvailable());
        verify(roomRepository, times(1)).save(existingRoom);
    }

    // 🧩 Test #5: Throw exception when updating non-existent room
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentRoom() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        Room updatedRoom = new Room(RoomType.Deluxe, 200.0, true);

        assertThrows(RuntimeException.class, () -> roomService.updateRoom(99L, updatedRoom));
        verify(roomRepository, never()).save(any(Room.class));
    }

    // 🧩 Test #6: Delete room
    @Test
    void shouldDeleteRoomSuccessfully() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roomRepository).deleteById(1L);

        roomService.deleteRoom(1L);

        verify(roomRepository, times(1)).deleteById(1L);
    }

    // 🧩 Test #7: Throw exception when deleting missing room
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentRoom() {
        when(roomRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> roomService.deleteRoom(99L));
        verify(roomRepository, never()).deleteById(99L);
    }

    @Test
    void shouldUpdateRoomStatus() {
        // given
        Room room = new Room();
        room.setId(1L);
        room.setRoomName("101");
        room.setStatus(RoomStatus.CLEAN);
        // when
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        // then
        Room result = roomService.updateRoomStatus(1L,RoomStatus.IN_MAINTENANCE);
        assertThat(result.getStatus()).isEqualTo(RoomStatus.IN_MAINTENANCE);
    }
}
