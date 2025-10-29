import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.application.HousekeepingService;
import pl.piwowarski.model.employees.Employee;
import pl.piwowarski.model.employees.EmployeeRole;
import pl.piwowarski.model.housekeeping.HousekeepingTask;
import pl.piwowarski.model.housekeeping.TaskStatus;
import pl.piwowarski.model.room.Room;
import pl.piwowarski.model.room.RoomStatus;
import pl.piwowarski.repositories.EmployeeRepository;
import pl.piwowarski.repositories.HousekeepingTaskRepository;
import pl.piwowarski.repositories.RoomRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HousekeepingServiceTest {

    @Mock
    RoomRepository roomRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    HousekeepingTaskRepository taskRepository;

    @InjectMocks
    HousekeepingService housekeepingService;

    @Test
    void shouldAssignCleanerToDirtyRoom() {
        // given
        Room room = new Room();
        room.setId(1L);
        room.setStatus(RoomStatus.DIRTY);

        Employee cleaner = new Employee();
        cleaner.setEmployeeID(10L);
        cleaner.setRole(EmployeeRole.CLEANER);
        // when
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(cleaner));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        HousekeepingTask task = housekeepingService.assignCleanerToRoom(1L, 10L);
        // then
        assertThat(task.getRoom().getId()).isEqualTo(1L);
        assertThat(task.getAssignedCleaner().getEmployeeID()).isEqualTo(10);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void shouldStartCleaningTask() {
        // given
        Room room = new Room();
        room.setId(1L);
        room.setStatus(RoomStatus.DIRTY);

        Employee cleaner = new Employee();
        cleaner.setEmployeeID(10L);
        cleaner.setRole(EmployeeRole.CLEANER);

        HousekeepingTask task = new HousekeepingTask();
        task.setId(100L);
        task.setRoom(room);
        task.setAssignedCleaner(cleaner);
        task.setStatus(TaskStatus.PENDING);
        // when
        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        HousekeepingTask result = housekeepingService.startCleaning(100L);
        // then
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void shouldFinishCleaningAndSetRoomToClean() {
        // given
        Room room = new Room();
        room.setId(1L);
        room.setStatus(RoomStatus.DIRTY);

        Employee cleaner = new Employee();
        cleaner.setEmployeeID(10L);
        cleaner.setRole(EmployeeRole.CLEANER);

        HousekeepingTask task = new HousekeepingTask();
        task.setId(100L);
        task.setRoom(room);
        task.setAssignedCleaner(cleaner);
        task.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(roomRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        HousekeepingTask result = housekeepingService.finishCleaning(100L);

        // then
        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(room.getStatus()).isEqualTo(RoomStatus.CLEAN);
        verify(roomRepository, times(1)).save(room);
    }
}
