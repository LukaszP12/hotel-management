package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.employees.Employee;
import pl.piwowarski.model.employees.EmployeeRole;
import pl.piwowarski.model.employees.housekeeping.HousekeepingTask;
import pl.piwowarski.model.employees.housekeeping.TaskStatus;
import pl.piwowarski.model.room.Room;
import pl.piwowarski.model.room.RoomStatus;
import pl.piwowarski.repositories.EmployeeRepository;
import pl.piwowarski.repositories.HousekeepingTaskRepository;
import pl.piwowarski.repositories.RoomRepository;

@Service
public class HousekeepingService {

    private final RoomRepository roomRepository;
    private final EmployeeRepository employeeRepository;
    private final HousekeepingTaskRepository taskRepository;

    public HousekeepingService(RoomRepository roomRepository,
                               EmployeeRepository employeeRepository,
                               HousekeepingTaskRepository taskRepository) {
        this.roomRepository = roomRepository;
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
    }

    public HousekeepingTask assignCleanerToRoom(Long roomId, Long cleanerId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (room.getStatus() != RoomStatus.DIRTY) {
            throw new IllegalStateException("Only DIRTY rooms can be assigned for cleaning.");
        }

        Employee cleaner = employeeRepository.findById(cleanerId)
                .orElseThrow(() -> new IllegalArgumentException("Cleaner not found"));

        if (cleaner.getRole() != EmployeeRole.CLEANER) {
            throw new IllegalStateException("Employee is not a cleaner");
        }

        // this prevents duplicate cleaning tasks
        taskRepository.findByRoomIdAndStatus(roomId, TaskStatus.PENDING)
                .ifPresent(task -> {
                    throw new IllegalStateException("Room already has a pending cleaning task");
                });

        HousekeepingTask task = new HousekeepingTask();
        task.setRoom(room);
        task.setAssignedCleaner(cleaner);
        task.setStatus(TaskStatus.PENDING);

        return taskRepository.save(task);
    }
}
