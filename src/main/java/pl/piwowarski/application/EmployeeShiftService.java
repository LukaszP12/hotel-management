package pl.piwowarski.application;

import org.springframework.stereotype.Service;
import pl.piwowarski.model.employees.Employee;
import pl.piwowarski.model.employees.EmployeeShift;
import pl.piwowarski.repositories.EmployeeRepository;
import pl.piwowarski.repositories.EmployeeShiftRepository;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class EmployeeShiftService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeShiftRepository shiftRepository;

    public EmployeeShiftService(EmployeeRepository employeeRepository, EmployeeShiftRepository shiftRepository) {
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
    }

    public EmployeeShift assignShift(long employeeId, LocalDate shiftDate, LocalTime start, LocalTime end) {
        if (end.isBefore(start) || end.equals(start)) {
            throw new IllegalArgumentException("Shift end time must be after start time");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        EmployeeShift shift = new EmployeeShift(employee, shiftDate, start, end);
        return shiftRepository.save(shift);
    }
}
