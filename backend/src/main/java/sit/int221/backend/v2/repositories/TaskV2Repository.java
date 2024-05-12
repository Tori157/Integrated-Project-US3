package sit.int221.backend.v2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.backend.v2.entities.TaskV2;

public interface TaskV2Repository extends JpaRepository<TaskV2, Integer> {
}
