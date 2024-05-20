package sit.int221.backend.v2.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.exceptions.BadRequestException;
import sit.int221.backend.service.ListMapper;
import sit.int221.backend.v2.dtos.NewTaskV2DTO;
import sit.int221.backend.v2.dtos.AllTaskV2DTO;
import sit.int221.backend.v2.entities.TaskV2;
import sit.int221.backend.v2.services.TaskV2Service;

import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
// @CrossOrigin(origins = {"http://localhost:5173","http://ip23us3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th/us3/"})
@RequestMapping("/v2/tasks")
public class TaskV2Controller {
    @Autowired
    private TaskV2Service taskV2Service;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @GetMapping("")
    public ResponseEntity<List<AllTaskV2DTO>> getAllTasks(
            @RequestParam(required = false) List<Integer> statusId,
//            @RequestParam(required = false) List<String> filterStatus,
            @RequestParam(defaultValue = "createdOn") String[] sortBy,
            @RequestParam(defaultValue = "ASC") String[] direction,
            @RequestParam Map<String, String> allParameters) {
        validateGetAllTasksParameters(allParameters);
        return ResponseEntity.ok(taskV2Service.sortTasksByStatusId(statusId, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskV2> getTaskById(@PathVariable Integer id) {
        TaskV2 task = taskV2Service.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<NewTaskV2DTO> addNewTask(@RequestBody NewTaskV2DTO newTask) {
        NewTaskV2DTO task = taskV2Service.createNewTask(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewTaskV2DTO> updateTask(@PathVariable Integer id, @RequestBody NewTaskV2DTO newTask) {
        return ResponseEntity.ok(taskV2Service.updateTaskById(id, newTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AllTaskV2DTO> removeTask(@PathVariable Integer id) {
        return ResponseEntity.ok(taskV2Service.removeTaskById(id));
    }

    public void validateGetAllTasksParameters(Map<String, String> allParameters) {
        List<String> validParams = Arrays.asList("statusId", "sortBy", "direction");
        for (String param : allParameters.keySet()) {
            if (!validParams.contains(param)) {
                throw new BadRequestException("Invalid filter parameter");
            }
        }
    }
}
