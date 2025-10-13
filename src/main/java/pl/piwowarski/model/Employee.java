package pl.piwowarski.model;

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

}
