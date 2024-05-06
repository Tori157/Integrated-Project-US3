package sit.int221.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.backend.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
