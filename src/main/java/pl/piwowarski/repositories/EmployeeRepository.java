package pl.piwowarski.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piwowarski.model.employees.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
