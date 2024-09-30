package sit.int221.backend.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.AddEditBoardDTO;
import sit.int221.backend.dtos.BoardDTO;
import sit.int221.backend.dtos.VisibilityDTO;
import sit.int221.backend.services.BoardService;
import sit.int221.backend.user_account.User;
import sit.int221.backend.utils.BoardAccessVerifier;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
@AllArgsConstructor
public class BoardController {

    private BoardService boardService;
    private BoardAccessVerifier boardAccessVerifier;

    @GetMapping
    public List<BoardDTO> getAllBoardsByUser(@AuthenticationPrincipal User user) {
        return boardService.getAllBoardsByUserId(user.getOid());
    }

    @GetMapping("/{id}")
    public BoardDTO getBoardId(@PathVariable String id) {
        boardAccessVerifier.verifyUserBoardAccess(id, false);
        return boardService.getBoardByUserAndId(id);
    }

    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody AddEditBoardDTO boardDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(boardDTO, user.getOid()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BoardDTO> updateBoardVisibility(@PathVariable("id") String boardId,
                                                          @AuthenticationPrincipal User user,
                                                          @RequestBody VisibilityDTO visibilityDTO) {
        boardAccessVerifier.verifyUserBoardAccess(boardId, true);
        BoardDTO updatedBoard = boardService.updateBoardVisibility(user.getOid(), boardId, visibilityDTO);
        return ResponseEntity.ok(updatedBoard);
    }
}
