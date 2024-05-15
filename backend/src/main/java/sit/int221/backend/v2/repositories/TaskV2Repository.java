package sit.int221.backend.v2.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.backend.v2.entities.Status;
import sit.int221.backend.v2.entities.TaskV2;

import java.util.List;

public interface TaskV2Repository extends JpaRepository<TaskV2, Integer> {
    List<TaskV2> findAllByStatus(Status status);

    @Query("SELECT t FROM TaskV2 t WHERE t.status.name IN :names")
    List<TaskV2> findAllByStatusName(@Param("names") List<String> names, Sort sort);
}
