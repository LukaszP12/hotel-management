package pl.piwowarski.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piwowarski.model.employees.housekeeping.HousekeepingTask;
import pl.piwowarski.model.employees.housekeeping.TaskStatus;

import java.util.Optional;

public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    Optional<HousekeepingTask> findByRoomIdAndStatus(Long roomId, TaskStatus status);
}
