package sit.int221.backend.v2.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.service.ListMapper;
import sit.int221.backend.v2.dtos.NewTaskV2DTO;
import sit.int221.backend.v2.dtos.AllTaskV2DTO;
import sit.int221.backend.v2.entities.TaskV2;
import sit.int221.backend.v2.services.TaskV2Service;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23us3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th/us3/"})
@RequestMapping("/v2/tasks")
public class TaskV2Controller {
    @Autowired
    private TaskV2Service taskV2Service;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @GetMapping("")
    public List<AllTaskV2DTO> getAllTasks() {
        return taskV2Service.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer id) {
        TaskV2 task = taskV2Service.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<NewTaskV2DTO> addNewTask(@RequestBody NewTaskV2DTO newTask) {
        NewTaskV2DTO task = taskV2Service.createNewTask(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public NewTaskV2DTO updateTask(@PathVariable Integer id, @RequestBody NewTaskV2DTO newTask) {
        return taskV2Service.updateTaskById(id, newTask);
    }

    @DeleteMapping("/{id}")
    public AllTaskV2DTO removeTask(@PathVariable Integer id) {
        return taskV2Service.removeTaskById(id);
    }
}
