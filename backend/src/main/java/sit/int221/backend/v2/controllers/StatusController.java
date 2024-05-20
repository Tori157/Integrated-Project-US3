package sit.int221.backend.v2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.backend.v2.services.StatusService;
import sit.int221.backend.v2.entities.Status;

import java.util.List;

@RestController
// @CrossOrigin(origins = {"http://localhost:5173", "http://ip23us3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th/us3/"})
@RequestMapping("/v2/statuses")
public class StatusController {
    @Autowired
    private StatusService statusService;

    @GetMapping("")
    public List<Status> getAllStatuses() {
        return statusService.getAllStatus();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getStatusById(@PathVariable Integer id) {
        Status status = statusService.getStatusById(id);
        return ResponseEntity.ok(status);
    }

    @PostMapping("")
    public ResponseEntity<Status> addNewStatus(@RequestBody Status status) {
        Status newStatus = statusService.createNewStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(newStatus);
    }

    @PutMapping("/{id}")
    public Status updateStatus(@PathVariable Integer id, @RequestBody Status status) {
        return statusService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void removeStatus(@PathVariable Integer id) {
        statusService.removeStatus(id);
    }

    @DeleteMapping("/{id}/{newId}")
    public void removeStatusTransfer(@PathVariable Integer id, @PathVariable Integer newId) {
        statusService.removeStatusTransfer(id, newId);
    }

}

