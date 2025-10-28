package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.Room;
import pl.piwowarski.repositories.RoomRepository;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id = " + id));
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room updateRoom(Long id, Room updateRoom) {
        Room exisitingRoom = getRoomById(id);
        exisitingRoom.setName(updateRoom.getRoomName());
        exisitingRoom.setCapacity(updateRoom.getCapacity());
        exisitingRoom.setRoomType(updateRoom.getRoomType());
        return roomRepository.save(exisitingRoom);
    }
}
