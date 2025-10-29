import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piwowarski.model.employees.Employee;
import pl.piwowarski.model.employees.EmployeeRole;
import pl.piwowarski.repositories.EmployeeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeShiftServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeShiftRepository shiftRepository;

    @InjectMocks
    private EmployeeShiftService shiftService;

    @Test
    void shouldAssignShiftToEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeID(1);
        employee.setFirstName("John");
        employee.setRole(EmployeeRole.CLEANER);

        LocalDate shiftDate = LocalDate.of(2025, 2, 15);
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(16, 0);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(shiftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeShift shift = shiftService.assignShift(1, shiftDate, start, end);

        assertThat(shift.getEmployee().getEmployeeID()).isEqualTo(1);
        assertThat(shift.getShiftDate()).isEqualTo(shiftDate);
        assertThat(shift.getStartTime()).isEqualTo(start);
        assertThat(shift.getEndTime()).isEqualTo(end);
    }
}
