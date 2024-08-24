package sit.int221.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.dtos.AddEditStatusDTO;
import sit.int221.backend.dtos.StatusDTO;
import sit.int221.backend.exceptions.DuplicatedStatusException;
import sit.int221.backend.project_management.Status;
import sit.int221.backend.project_management.Task;
import sit.int221.backend.exceptions.IllegalDeleteStatusException;
import sit.int221.backend.exceptions.IllegalStatusTransferException;
import sit.int221.backend.exceptions.StatusWithIdNotFoundException;
import sit.int221.backend.project_management.StatusRepository;
import sit.int221.backend.project_management.TaskRepository;

import java.util.List;

@Service
public class StatusService {

    private StatusRepository statusRepository;
    private TaskRepository taskRepository;
    private TaskService taskService;
    private ModelMapper modelMapper;

    @Autowired
    public StatusService(StatusRepository statusRepository, TaskRepository taskRepository, @Lazy TaskService taskService, ModelMapper modelMapper) {
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.modelMapper = modelMapper;

    }

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }


    public Status getStatusById(int id) {
        return statusRepository.findById(id).orElseThrow(
                () -> new StatusWithIdNotFoundException("status does not exist")
        );
    }

    @Transactional
    public Status createStatus(AddEditStatusDTO status) {
//        if (status.getDescription() != null) {
//            status.setDescription(status.getDescription());
//
//        }
//        return statusRepository.save(status);
        if (statusRepository.existsByNameIgnoreCase(status.getName())) {
            throw new DuplicatedStatusException("Status name must be unique");
        }
        Status newStatus = modelMapper.map(status, Status.class);
        return statusRepository.save(newStatus);
    }

    @Transactional
    public Status removeStatusById(int id) {
        Status existingStatus = getStatusById(id);
        if (taskService.hasTaskWithStatus(id)) {
            throw new IllegalDeleteStatusException("Cannot delete status because there are tasks associated with this status.");
        }

        statusRepository.delete(existingStatus);
        return existingStatus;
//        if (id == 1) {
//            throw new BadRequestException("The 'No Status' cannot be delete");
//        }
//        Status status = statusRepository.findById(id).orElseThrow(
//                () -> new ItemNotFoundException("NOT FOUND")
//        );
//
//        statusRepository.delete(status);
    }

    @Transactional
    public Status removeStatusTransfer(int id, int newId) {
        if (id == newId) {
            throw new IllegalStatusTransferException("Destination status for task transfer must be different from current status.");
        }

        Status oldStatus = statusRepository.findById(id).orElseThrow(
                () -> new StatusWithIdNotFoundException("status does not exist")
        );
        Status newStatus = statusRepository.findById(newId).orElseThrow(
                () -> new StatusWithIdNotFoundException("status does not exist")
        );

        List<Task> tasks = taskRepository.findAllByStatus(oldStatus);

        for (Task task : tasks) {
            task.setStatus(newStatus);
        }
        taskRepository.saveAll(tasks);
        statusRepository.delete(oldStatus);
        return oldStatus;
    }

    @Transactional
    public StatusDTO updateStatus(int id, AddEditStatusDTO newStatus) {
        if (statusRepository.existsByNameIgnoreCase(newStatus.getName())) {
            throw new DuplicatedStatusException("Status name must be unique");
        }

        Status status = modelMapper.map(newStatus, Status.class);
        status.setId(id);
        Status updatedStatus = statusRepository.save(status);
        return modelMapper.map(updatedStatus, StatusDTO.class);
//        if( id == 1) {
//            throw new BadRequestException("The 'No Status' cannot be edited");
//        }
//        statusRepository.findById(id).orElseThrow(
//                () -> new ItemNotFoundException("NOT FOUND")
//        );
//        return statusRepository.save(status);

    }
}
