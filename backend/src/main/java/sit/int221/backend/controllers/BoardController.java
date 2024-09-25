package sit.int221.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.BoardDTO;
import sit.int221.backend.services.BoardService;
import sit.int221.backend.user_account.User;

import java.util.List;

@RestController
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping
    public List<BoardDTO> getAllBoardsByUser(@AuthenticationPrincipal User user) {
        return boardService.getAllBoardsByUserId(user.getOid());
    }

    @GetMapping("/{id}")
    public BoardDTO getBoardId(@PathVariable String id, @AuthenticationPrincipal User user) {
        return boardService.getBoardByUserAndId(user.getOid(), id);
    }

    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody BoardDTO boardDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(boardDTO, user.getOid()));
    }
}
