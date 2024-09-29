package sit.int221.backend.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sit.int221.backend.exceptions.BoardAccessDeniedException;
import sit.int221.backend.exceptions.NotFoundException;
import sit.int221.backend.exceptions.UserNotAuthenticatedException;
import sit.int221.backend.project_management.Board;
import sit.int221.backend.project_management.Visibility;
import sit.int221.backend.services.BoardService;

@Service
@AllArgsConstructor
public class BoardAccessVerifier {
    private final BoardService boardService;

    public void verifyUserBoardAccess(String boardId, boolean requireOwner) {
        Board board = boardService.getBoardById(boardId)
                .orElseThrow(() -> new NotFoundException("Board id '" + boardId + "' not found"));

        if (isAccessAllowed(board, requireOwner)) {
            return;
        }

        throw new BoardAccessDeniedException("User does not have access to board id '" + boardId + "'");
    }

    private boolean isAccessAllowed(Board board, boolean requireOwner) {
        Visibility boardVisibility = board.getVisibility();

        if (boardVisibility != Visibility.PRIVATE && !requireOwner) {
            return true;
        }

        String userId = fetchAuthenticatedUserId();
        return board.getOwnerId().equals(userId);
    }

    private String fetchAuthenticatedUserId() {
        try {
            return SecurityUtils.getAuthenticatedUser().getOid();
        } catch (UserNotAuthenticatedException e) {
            throw new BoardAccessDeniedException("User cannot perform the operation to this board");
        }
    }
}