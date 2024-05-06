package sit.int221.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.AllTaskDTO;
import sit.int221.backend.exceptions.ErrorResponse;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.services.ListMapper;
import sit.int221.backend.services.TaskService;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/v1/tasks")
public class DeleteTaskController {
    @Autowired
    private TaskService service;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @DeleteMapping("/{id}")
    public AllTaskDTO removeTask(@PathVariable Integer id) {
        return service.removeTask(id);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(ItemNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(Timestamp.from(Instant.now()), HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI() );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
