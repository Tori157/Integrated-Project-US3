package sit.int221.backend.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.AddEditTaskDTO;
import sit.int221.backend.dtos.AllTasksDTO;
import sit.int221.backend.dtos.TaskDTO;
import sit.int221.backend.exceptions.DuplicatedStatusException;
import sit.int221.backend.project_management.Task;
import sit.int221.backend.services.BoardTaskService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v3/boards/{boardId}/tasks")
@AllArgsConstructor
public class BoardTaskController {
    private BoardTaskService boardTaskService;

    @GetMapping
    public ResponseEntity<List<AllTasksDTO>> getAllTasksForBoard(
            @PathVariable String boardId,
            @RequestParam(required = false) List<String> filterStatuses,
            @RequestParam(defaultValue = "createdOn") String[] sortBy,
            @RequestParam(defaultValue = "ASC") String[] direction,
            @RequestParam Map<String, String> allParameters) {
        validateGetAllTasksParameters(allParameters);
        return ResponseEntity.ok(boardTaskService.sortTasksByStatusName(boardId, filterStatuses, sortBy, direction));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer taskId, @PathVariable String boardId) {
        return ResponseEntity.ok(boardTaskService.getTaskById(taskId, boardId));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@PathVariable String boardId, @Valid @RequestBody AddEditTaskDTO newTask) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardTaskService.createTask(boardId, newTask));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTaskById(@PathVariable Integer taskId, @PathVariable String boardId, @Valid @RequestBody AddEditTaskDTO taskDTO) {
        return ResponseEntity.ok(boardTaskService.updateTaskById(taskId, boardId, taskDTO));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<AllTasksDTO> removeTaskById(@PathVariable Integer taskId, @PathVariable String boardId) {
        return ResponseEntity.ok(boardTaskService.removeTaskById(taskId, boardId));
    }

    public void validateGetAllTasksParameters(Map<String, String> allParameters) {
        List<String> validParams = Arrays.asList("filterStatuses", "sortBy", "direction");
        for (String param : allParameters.keySet()) {
            if (!validParams.contains(param)) {
                throw new DuplicatedStatusException("Invalid filter parameter");
            }
        }
    }
}
