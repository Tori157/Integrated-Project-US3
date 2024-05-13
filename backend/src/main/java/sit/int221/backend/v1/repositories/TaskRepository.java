package sit.int221.backend.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.backend.v1.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
