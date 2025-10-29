package pl.piwowarski.model.employees;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int EmployeeID;

    private String FirstName;

    private String LastName;

    private String age;

    private int WorkHours;

    private EmployeeRole employeeRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;

    public void setEmployeeID(int EmployeeID) {
        this.EmployeeID = EmployeeID;
    }

    public void setRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
    }

    public EmployeeRole getRole() {
        return employeeRole;
    }
}
