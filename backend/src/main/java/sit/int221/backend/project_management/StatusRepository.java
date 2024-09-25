package sit.int221.backend.project_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    @Query("SELECT COUNT(s) > 0 FROM Status s WHERE LOWER(s.name) = LOWER(:name) AND LOWER(:name) = LOWER('No Status')")
    boolean findStatusWithNameNoStatus(@Param("name") String name);

    boolean existsByNameAndBoard_BoardId(String name, String board);
}
