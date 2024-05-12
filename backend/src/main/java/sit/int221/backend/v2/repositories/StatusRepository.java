package sit.int221.backend.v2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.backend.v2.entities.Status;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}
