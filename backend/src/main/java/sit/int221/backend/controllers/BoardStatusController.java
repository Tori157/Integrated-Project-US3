package sit.int221.backend.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.AddEditStatusDTO;
import sit.int221.backend.dtos.StatusDTO;
import sit.int221.backend.project_management.Status;
import sit.int221.backend.services.BoardStatusService;
import sit.int221.backend.utils.BoardAccessVerifier;

import java.util.List;

@RestController
@RequestMapping("/v3/boards/{boardId}/statuses")
@AllArgsConstructor
public class BoardStatusController {
    private final BoardStatusService boardStatusService;
    private BoardAccessVerifier boardAccessVerifier;

    @GetMapping
    public ResponseEntity<List<Status>> findAllByBoardId(@PathVariable String boardId) {
        boardAccessVerifier.verifyUserBoardAccess(boardId, false);
        return ResponseEntity.ok(boardStatusService.findAllByBoardId(boardId));
    }

    @GetMapping("/{statusId}")
    public ResponseEntity<Status> findByIdAndBoardId(@PathVariable Integer statusId, @PathVariable String boardId) {
        boardAccessVerifier.verifyUserBoardAccess(boardId, false);
        return ResponseEntity.ok(boardStatusService.findByIdAndBoardId(statusId, boardId));
    }

    @PostMapping
    public ResponseEntity<Status> createStatus(@Valid @RequestBody AddEditStatusDTO statusDTO, @PathVariable String boardId) {
        boardAccessVerifier.verifyUserBoardAccess(boardId, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardStatusService.createStatus(statusDTO, boardId));
    }

    @PutMapping("/{statusId}")
    public ResponseEntity<StatusDTO> updateStatusById(@PathVariable Integer statusId, @PathVariable String boardId, @Valid @RequestBody AddEditStatusDTO statusDTO) {
        boardAccessVerifier.verifyUserBoardAccess(boardId, true);
        return ResponseEntity.ok(boardStatusService.updateStatus(statusId, boardId, statusDTO));
    }

    @DeleteMapping("/{statusId}")
    public ResponseEntity<Status> deleteStatusById(@PathVariable Integer statusId, @PathVariable String boardId) {
        boardAccessVerifier.verifyUserBoardAccess(boardId, true);
        return ResponseEntity.ok(boardStatusService.deleteStatusById(statusId, boardId));
    }

    @DeleteMapping("/{statusId}/{newStatusId}")
    public ResponseEntity<Status> transferAndDeleteStatus(@PathVariable Integer statusId, @PathVariable Integer newStatusId, @PathVariable String boardId) {
        boardAccessVerifier.verifyUserBoardAccess(boardId, true);
        Status transferAndDeleteStatus = boardStatusService.transferAndDeleteStatus(statusId, newStatusId, boardId);
        return ResponseEntity.ok(transferAndDeleteStatus);
    }
}
