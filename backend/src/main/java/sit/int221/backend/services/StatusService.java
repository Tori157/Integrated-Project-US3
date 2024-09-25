package sit.int221.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.dtos.AddEditStatusDTO;
import sit.int221.backend.dtos.StatusDTO;
import sit.int221.backend.exceptions.DuplicatedStatusException;
import sit.int221.backend.project_management.*;
import sit.int221.backend.exceptions.IllegalDeleteStatusException;
import sit.int221.backend.exceptions.IllegalStatusTransferException;
import sit.int221.backend.exceptions.StatusWithIdNotFoundException;

import java.util.Arrays;
import java.util.*;

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
        if (statusRepository.findStatusWithNameNoStatus(status.getName())) {
            throw new DuplicatedStatusException("The 'No Status' must be unique");
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
        if (statusRepository.findStatusWithNameNoStatus(newStatus.getName())) {
            throw new DuplicatedStatusException("The 'No Status' must be unique");
        }
        System.out.println(statusRepository.findStatusWithNameNoStatus(newStatus.getName()));
        Status status = modelMapper.map(newStatus, Status.class);
        status.setId(id);
        Status updatedStatus = statusRepository.save(status);
        return modelMapper.map(updatedStatus, StatusDTO.class);
    }

    @Transactional
    public void createDefaultStatuses(Board board) {
        String boardId = board.getId();
        List<Status> defaultStatuses = Arrays.asList(
                buildStatus("No Status", "The default status", board),
                buildStatus("To Do", null, board),
                buildStatus("Doing", "Being worked on", board),
                buildStatus("Done", "Finished", board)
        );

        for (Status status : defaultStatuses) {
            boolean exists = statusRepository.existsByNameAndBoardId(status.getName(), boardId);
            if (!exists) {
                statusRepository.save(status);
            }
        }

        statusRepository.saveAll(defaultStatuses);
    }

    private Status buildStatus(String name, String description, Board board) {
        Status status = new Status();
        status.setName(name);
        status.setDescription(description);
        status.setBoard(board);
        return status;
    }
}
