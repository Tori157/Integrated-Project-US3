package sit.int221.backend.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.*;
import sit.int221.backend.exceptions.DuplicatedStatusException;
import sit.int221.backend.services.TaskService;
import sit.int221.backend.utils.ListMapper;
import sit.int221.backend.project_management.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = {"http://localhost:5173", "http://ip23us3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th/us3/"})
@RequestMapping("/v2/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @GetMapping("")
    public ResponseEntity<List<AllTasksDTO>> getAllTasks(
            @RequestParam(required = false) List<String> filterStatuses,
            @RequestParam(defaultValue = "createdOn") String[] sortBy,
            @RequestParam(defaultValue = "ASC") String[] direction,
            @RequestParam Map<String, String> allParameters) {
        validateGetAllTasksParameters(allParameters);
        return ResponseEntity.ok(taskService.sortTasksByStatusName(filterStatuses, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<TaskDTO> addNewTask(@Valid @RequestBody AddEditTaskDTO newTask) {
        TaskDTO task = taskService.createTask(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id, @Valid @RequestBody AddEditTaskDTO newTask) {
        return ResponseEntity.ok(taskService.updateTaskById(id, newTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AllTasksDTO> removeTask(@PathVariable Integer id) {
        AllTasksDTO deletedTask = taskService.removeTaskById(id);
        return ResponseEntity.ok(deletedTask);
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
