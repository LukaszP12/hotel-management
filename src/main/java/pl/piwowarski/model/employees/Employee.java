package pl.piwowarski.model.employees;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import pl.piwowarski.model.housekeeping.HousekeepingTask;

import java.util.List;

@Entity(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private int workHours;

    private String age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;

    @OneToMany(mappedBy = "assignedCleaner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HousekeepingTask> housekeepingTasks;

    public void setEmployeeID(Long EmployeeID) {
        this.id = EmployeeID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setRole(EmployeeRole employeeRole) {
        this.role = employeeRole;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public Long getEmployeeID() {
        return id;
    }
}
