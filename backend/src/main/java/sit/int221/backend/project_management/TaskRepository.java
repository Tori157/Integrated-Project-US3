package sit.int221.backend.project_management;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByStatus(Status status);

    @Query("SELECT t FROM Task t WHERE t.status.name IN :names")
    List<Task> findAllByStatusName(@Param("names") List<String> names, Sort sort);

    @Query("SELECT t FROM Task t WHERE t.status.id IN :id")
    List<Task> findAllByStatusId(@Param("id") List<Integer> statusId, Sort sort);

    @Query("SELECT t FROM Task t WHERE t.status.id IN :id  And t.board.id = :boardId")
    List<Task> findAllByStatusIdAndBoardId(@Param("id") List<Integer> statusId, String boardId, Sort sort);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM Task t WHERE t.status.id = :statusId")
    boolean existsByStatusId(@Param("statusId") Integer statusId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.status.id = ?1")
    long countByStatusId(Integer statusId);

    @Query("SELECT t FROM Task t WHERE t.board.id = :boardId")
    List<Task> findAllByBoardId(@Param("boardId") String boardId, Sort sort);

    @Query("SELECT t FROM Task t WHERE  t.id = :taskId And t.board.id = :boardId ")
    Task findByBoardIdAndTaskId(@Param("taskId") Integer taskId, @Param("boardId") String boardId);

    @Query("SELECT t FROM Task t WHERE t.board.id = :boardId And t.status.name IN :names")
    List<Task> findAllByBoardIdAndStatusName(String boardId, List<String> names, Sort sort);
}
