package sit.int221.itbkkbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
