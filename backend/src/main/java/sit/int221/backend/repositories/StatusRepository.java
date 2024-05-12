package sit.int221.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.backend.entities.Status;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}
