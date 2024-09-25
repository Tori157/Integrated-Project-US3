package sit.int221.backend.project_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    @Query("SELECT COUNT(s) > 0 FROM Status s WHERE LOWER(s.name) = LOWER(:name) AND LOWER(:name) = LOWER('No Status')")
    boolean findStatusWithNameNoStatus(@Param("name") String name);

    boolean existsByNameAndBoardId(String name, String board);

    boolean existsByNameIgnoreCaseAndBoardId(String name, String boardId);
    boolean existsByIdAndBoardId(Integer statusId, String boardId);

    List<Status> findAllByBoardId(String boardId);

    Optional<Status> findByIdAndBoardId(Integer statusId, String boardId);
}
