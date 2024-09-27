package sit.int221.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.AddEditBoardDTO;
import sit.int221.backend.dtos.BoardDTO;
import sit.int221.backend.dtos.VisibilityDTO;
import sit.int221.backend.exceptions.BoardAccessDeniedException;
import sit.int221.backend.exceptions.ErrorResponse;
import sit.int221.backend.exceptions.NotFoundException;
import sit.int221.backend.project_management.Visibility;
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
//        try {
//            BoardDTO boardDTO = boardService.getBoardByUserAndId(user.getOid(), id);
//            return ResponseEntity.ok(boardDTO);
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        } catch (BoardAccessDeniedException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
//        }
        return boardService.getBoardByUserAndId(user.getOid(), id);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<BoardDTO> getBoardById(@PathVariable String id, @AuthenticationPrincipal User user) {
//
//        if (boardService.getBoardById(id).isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        BoardDTO boardDTO = boardService.getBoardByUserAndId(user.getOid(), id);
//        System.out.println("check condition");
//
//        if (boardDTO.getVisibility().equals("PUBLIC") || boardDTO.getOwner().map(owner -> owner.getOid().equals(user.getOid())).orElse(false)) {
//            return ResponseEntity.ok(boardDTO);
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//    }

    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody AddEditBoardDTO boardDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(boardDTO, user.getOid()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BoardDTO> updateBoardVisibility(@PathVariable String id, @RequestBody VisibilityDTO requestBody, @AuthenticationPrincipal User user) {

        //404
        if (boardService.getBoardById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        BoardDTO boardDTO = boardService.getBoardByUserAndId(user.getOid(), id);
//        if (boardDTO == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }

        //403
//        boolean isOwner = boardDTO.getOwner().map(owner -> owner.getOid().equals(user.getOid())).orElse(false);
        if (!boardDTO.getOwner().map(owner -> owner.getOid().equals(user.getOid())).orElse(false)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Visibility visibility = requestBody.getVisibility();

        //400
        if (visibility != Visibility.PRIVATE && visibility != Visibility.PUBLIC) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boardService.updateBoardVisibility(id, visibility);

        //200
        return ResponseEntity.ok(boardService.getBoardByUserAndId(user.getOid(), id));
    }
}
