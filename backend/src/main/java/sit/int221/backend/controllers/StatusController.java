package sit.int221.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.dtos.AddEditStatusDTO;
import sit.int221.backend.dtos.StatusDTO;
import sit.int221.backend.services.StatusService;
import sit.int221.backend.project_management.Status;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:5173", "http://ip23us3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th/us3/"})
@RequestMapping("/v2/statuses")
public class StatusController {
    @Autowired
    private StatusService statusService;

    @GetMapping("")
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(statusService.getAllStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Status> getStatusById(@PathVariable Integer id) {
        return ResponseEntity.ok(statusService.getStatusById(id));
    }

    @PostMapping("")
    public ResponseEntity<Status> addNewStatus(@Valid @RequestBody AddEditStatusDTO status) {
        return ResponseEntity.status(HttpStatus.CREATED).body(statusService.createStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusDTO> updateStatus(@PathVariable Integer id, @Valid @RequestBody AddEditStatusDTO status) {
        return ResponseEntity.ok(statusService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Status> removeStatus(@PathVariable Integer id) {
        Status deletedStatus = statusService.removeStatusById(id);
        return ResponseEntity.ok(deletedStatus);
    }

    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<Status> removeStatusTransfer(@PathVariable Integer id, @PathVariable Integer newId) {
        Status removeStatusTransfer = statusService.removeStatusTransfer(id, newId);
        return ResponseEntity.ok(removeStatusTransfer);
    }

}

