import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.model.employees.Employee;
import pl.piwowarski.model.employees.EmployeeRole;
import pl.piwowarski.model.employees.HousekeepingTask;
import pl.piwowarski.model.employees.TaskStatus;
import pl.piwowarski.model.room.Room;
import pl.piwowarski.model.room.RoomStatus;
import pl.piwowarski.repositories.RoomRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        cleaner.setEmployeeID(10);
        cleaner.setRole(EmployeeRole.CLEANER);
        // when
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(cleaner));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        HousekeepingTask task = housekeepingService.assignCleanerToRoom(1L, 10);
        // then
        assertThat(task.getRoom().getId()).isEqualTo(1L);
        assertThat(task.getAssignedCleaner().getEmployeeID()).isEqualTo(10);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
    }
}
