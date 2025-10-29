package pl.piwowarski.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piwowarski.model.employees.EmployeeShift;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Long> {
}
