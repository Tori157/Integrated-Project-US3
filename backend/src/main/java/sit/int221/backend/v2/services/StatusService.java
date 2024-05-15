package sit.int221.backend.v2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.exceptions.BadRequestException;
import sit.int221.backend.v2.entities.Status;
import sit.int221.backend.v2.entities.TaskV2;
import sit.int221.backend.v2.repositories.StatusRepository;
import sit.int221.backend.v2.repositories.TaskV2Repository;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskV2Repository taskV2Repository;

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }


    public Status getStatusById(int id) {
        return statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
    }

    @Transactional
    public Status createNewStatus(Status status) {
        if (status.getDescription() != null) {
            status.setDescription(status.getDescription());

        }
        return statusRepository.save(status);
    }

    @Transactional
    public void removeStatus(int id) {
        if (id == 1) {
            throw new BadRequestException("The 'No Status' cannot be delete");
        }
        Status status = statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );

        statusRepository.delete(status);
    }

    @Transactional
    public void removeStatusTransfer(int id, int newId) {
        Status oldStatus = statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        Status newStatus = statusRepository.findById(newId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );

        List<TaskV2> tasks = taskV2Repository.findAllByStatus(oldStatus);

        for (TaskV2 task : tasks) {
            task.setStatus(newStatus);
        }
        taskV2Repository.saveAll(tasks);
        statusRepository.delete(oldStatus);
    }

    @Transactional
    public Status updateStatus(int id, Status status) {
        if( id == 1) {
            throw new BadRequestException("The 'No Status' cannot be edited");
        }
        statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        return statusRepository.save(status);

    }
}
