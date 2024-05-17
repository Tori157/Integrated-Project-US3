package sit.int221.backend.v1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.v1.entities.Task;
import sit.int221.backend.v1.services.TaskService;
import sit.int221.backend.v1.dtos.AddEditTaskDTO;
import sit.int221.backend.v1.dtos.AllTaskDTO;
import sit.int221.backend.service.ListMapper;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
// @CrossOrigin(origins = {"http://localhost:5173","http://ip23us3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th/us3/"})
@RequestMapping("/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService service;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @GetMapping("")
    public List<AllTaskDTO> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer id) {
        Task task = service.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<AddEditTaskDTO> addNewTask(@RequestBody Task task) {
        AddEditTaskDTO newTask = service.createNewTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @PutMapping("/{id}")
    public AddEditTaskDTO updateTask(@PathVariable Integer id, @RequestBody Task task) {
        return service.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public AllTaskDTO removeTask(@PathVariable Integer id) {
        return service.removeTask(id);
    }

}
