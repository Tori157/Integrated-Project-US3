package sit.int221.backend.services;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.dtos.AddEditStatusDTO;
import sit.int221.backend.dtos.StatusDTO;
import sit.int221.backend.exceptions.*;
import sit.int221.backend.project_management.*;
import sit.int221.backend.utils.BoardAccessVerifier;

import java.util.List;

@Service
@AllArgsConstructor
public class BoardStatusService {
    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;
    private BoardAccessVerifier boardAccessVerifier;
    private final TaskService taskService;
    private final ModelMapper modelMapper;

    public List<Status> findAllByBoardId(String boardId) {
        return statusRepository.findAllByBoardId(boardId);
    }

    public Status findByIdAndBoardId(Integer statusId, String boardId) {
        return statusRepository.findByIdAndBoardId(statusId, boardId)
                .orElseThrow(() -> new NotFoundException("The requested status does not exist"));
    }

    @Transactional
    public Status createStatus(AddEditStatusDTO status, String boardId) {
        if (statusRepository.existsByNameIgnoreCaseAndBoardId(status.getName(), boardId)) {
            throw new DuplicatedStatusException("Status name must be unique");
        }

        Board board = Board.builder().id(boardId).build();
        Status newStatus = modelMapper.map(status, Status.class);
        newStatus.setBoard(board);
        return statusRepository.save(newStatus);
    }

    @Transactional
    public StatusDTO updateStatus(Integer statusId, String boardId, AddEditStatusDTO status) {
        if (statusRepository.existsByNameIgnoreCaseAndBoardId(status.getName(), boardId)) {
            throw new DuplicatedStatusException("Status name must be unique");
        }

        Status existingStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new NotFoundException("The requested status does not exist"));

        if (isStatsNotBelongingToBoard(existingStatus, boardId)) {
            throw new NotFoundException("The requested status does not exist");
        }

        existingStatus.setName(status.getName());
        existingStatus.setDescription(status.getDescription());
        Status updatedStatus = statusRepository.save(existingStatus);
        return modelMapper.map(updatedStatus, StatusDTO.class);
    }

    @Transactional
    public Status deleteStatusById(Integer statusId, String boardId) {
        if (taskService.hasTaskWithStatus(statusId)) {
            throw new IllegalDeleteStatusException("Cannot delete status because there are tasks associated with this status.");
        }
        Status existingStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new NotFoundException("The requested status does not exist"));
        if (isStatsNotBelongingToBoard(existingStatus, boardId)) {
            throw new NotFoundException("The requested status does not exist");
        }

        statusRepository.delete(existingStatus);
        return existingStatus;
    }

    @Transactional
    public Status transferAndDeleteStatus(Integer statusId, Integer newStatusId, String boardId) {
        if (statusId.equals(newStatusId)) {
            throw new IllegalStatusTransferException("Destination status for task transfer must be different from current status.");
        }

        if (!isStatusBelongingToBoard(statusId, boardId)) {
            throw new NotFoundException("The requested status does not exist");
        }

        Status oldStatus = statusRepository.findById(statusId).orElseThrow(
                () -> new StatusWithIdNotFoundException("status does not exist")
        );
        Status newStatus = statusRepository.findById(newStatusId).orElseThrow(
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

    private boolean isStatsNotBelongingToBoard(Status status, String boardId) {
        return !isStatusBelongingToBoard(status.getId(), boardId);
    }

    private boolean isStatusBelongingToBoard(Integer statusId, String boardId) {
        return statusRepository.existsByIdAndBoardId(statusId, boardId);
    }
}
