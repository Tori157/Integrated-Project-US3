package sit.int221.backend.v2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.backend.v2.entities.Status;
import sit.int221.backend.v2.entities.TaskV2;

import java.util.List;

public interface TaskV2Repository extends JpaRepository<TaskV2, Integer> {
    List<TaskV2> findAllByStatus(Status status);
}
