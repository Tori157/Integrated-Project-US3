package sit.int221.backend.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sit.int221.backend.exceptions.NotFoundException;
import sit.int221.backend.project_management.BoardRepository;

@Service
@AllArgsConstructor
public class BoardServiceUtil {
    private final BoardRepository boardRepository;

    public void verifyBoardExists(String boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new NotFoundException("Board with ID " + boardId + " does not exist.");
        }
    }
}