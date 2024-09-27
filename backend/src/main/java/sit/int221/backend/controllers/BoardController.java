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

//    @GetMapping("/{id}")
//    public BoardDTO getBoardId(@PathVariable String id, @AuthenticationPrincipal User user) {
//        return boardService.getBoardByUserAndId(user.getOid(), id);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardById(@PathVariable String id, @AuthenticationPrincipal User user) {

//        System.out.println("get board by id"+boardService.getBoardById(id).isEmpty());
        if (boardService.getBoardById(id).isEmpty()) {
//            System.out.println("Not found"+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
//        System.out.println("Found "+id);
        BoardDTO boardDTO = boardService.getBoardByUserAndId(user.getOid(), id);
        System.out.println("check condition");

        if (boardDTO.getVisibility().equals("PUBLIC") || boardDTO.getOwner().map(owner -> owner.getOid().equals(user.getOid())).orElse(false)) {
//            System.out.println("Found "+boardDTO);
            return ResponseEntity.ok(boardDTO);
        } else {
//            System.out.println("Forbidden "+boardDTO);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody AddEditBoardDTO boardDTO, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(boardDTO, user.getOid()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BoardDTO> updateBoardVisibility(@PathVariable String id, @RequestBody VisibilityDTO requestBody, @AuthenticationPrincipal User user) {

        //404
        if (boardService.getBoardById(id).isEmpty()) {
//            System.out.println("Not found"+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

//        System.out.println("Found "+id);

        BoardDTO boardDTO = boardService.getBoardByUserAndId(user.getOid(), id);
//        System.out.println("board "+boardDTO.getId());
//        if (boardDTO == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }

        //403
//        System.out.println("owner "+boardDTO.getOwner().map(owner -> owner.getOid()).orElse(null));
//        System.out.println("user "+user.getOid());
//        boolean isOwner = boardDTO.getOwner().map(owner -> owner.getOid().equals(user.getOid())).orElse(false);
//        System.out.println("owner "+isOwner);
        if (!boardDTO.getOwner().map(owner -> owner.getOid().equals(user.getOid())).orElse(false)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Visibility visibility = requestBody.getVisibility();
//        System.out.println("visibility "+visibility);

        //400
        if (visibility != Visibility.PRIVATE && visibility != Visibility.PUBLIC) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boardService.updateBoardVisibility(id, visibility);

        //200
        return ResponseEntity.ok(boardService.getBoardByUserAndId(user.getOid(), id));
    }
}
