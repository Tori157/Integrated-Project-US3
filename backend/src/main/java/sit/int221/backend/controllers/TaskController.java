package sit.int221.backend.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.SimpleAllTaskDTO;
import sit.int221.backend.entities.Task;
import sit.int221.backend.services.ListMapper;
import sit.int221.backend.services.TaskService;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService service;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @GetMapping("")
    public List<SimpleAllTaskDTO> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer id) {
        Task task = service.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public Task addNewTask(@RequestBody Task task) {
        return service.createNewTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody Task task) {
        return service.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public void removeTask(@PathVariable Integer id) {
        service.removeTask(id);
    }
}
