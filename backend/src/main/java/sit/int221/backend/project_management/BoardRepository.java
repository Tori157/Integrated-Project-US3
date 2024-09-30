package sit.int221.backend.project_management;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findAllByOwnerId(String ownerId);
    Optional<Board> findByIdAndOwnerId(String id, String ownerId);

    Optional<Board> findByIdAndVisibility(String id, Visibility visibility);
}