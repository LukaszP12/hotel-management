package pl.piwowarski.model.employees.housekeeping;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import pl.piwowarski.model.employees.Employee;
import pl.piwowarski.model.room.Room;

import java.time.LocalDateTime;

@Entity
public class HousekeepingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Room room;

    @ManyToOne
    private Employee assignedCleaner;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    private LocalDateTime assignedAt = LocalDateTime.now();

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setAssignedCleaner(Employee cleaner) {
        this.assignedCleaner = cleaner;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Room getRoom() {
        return room;
    }
}
