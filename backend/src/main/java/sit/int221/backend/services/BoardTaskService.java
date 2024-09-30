package sit.int221.backend.services;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.dtos.AddEditTaskDTO;
import sit.int221.backend.dtos.AllTasksDTO;
import sit.int221.backend.dtos.TaskDTO;
import sit.int221.backend.exceptions.NotFoundException;
import sit.int221.backend.project_management.*;
import sit.int221.backend.utils.BoardAccessVerifier;
import sit.int221.backend.utils.ListMapper;

import java.util.List;

@Service
@AllArgsConstructor
public class BoardTaskService {
    private TaskRepository taskRepository;
    private StatusService statusService;
    private BoardAccessVerifier boardAccessVerifier;
    private ModelMapper modelMapper;
    private ListMapper listMapper;

    public List<AllTasksDTO> getAllTasks(String boardId, String field, String order, List<String> filterStatuses) {
        List<Task> tasks;
        Sort sort = Sort.by(Sort.Direction.fromString(order), field);

        if (filterStatuses == null) {
            tasks = taskRepository.findAllByBoardId(boardId, sort);
        } else {
            tasks = taskRepository.findAllByBoardIdAndStatusName(boardId, filterStatuses, sort);
        }
        return listMapper.mapList(taskRepository.findAll(), AllTasksDTO.class, modelMapper);
//        return listMapper.mapList(taskRepository.findAll(Sort.by("createdOn").ascending()), AllTasksDTO.class, modelMapper);
    }


    public Task getTaskById(Integer taskId, String boardId) {
        Task foundTask = taskRepository.findByBoardIdAndTaskId(taskId, boardId);
        if (foundTask == null) {
            throw new NotFoundException("Task " + taskId + " does not exist !!!");
        }
        return modelMapper.map(foundTask, Task.class);
    }

    @Transactional
    public TaskDTO createTask(String boardId, AddEditTaskDTO newTask) {
        Status status = statusService.getStatusById(newTask.getStatus());
        Task task = modelMapper.map(newTask, Task.class);
        task.setStatus(status);

        Board board = new Board();
        board.setId(boardId);
        task.setBoard(board);

        return modelMapper.map(taskRepository.save(task), TaskDTO.class);
    }

    @Transactional
    public AllTasksDTO removeTaskById(Integer taskId, String boardId) {
        Task existingTask = taskRepository.findByBoardIdAndTaskId(taskId, boardId);

        if (existingTask == null) {
            throw new NotFoundException("does not exist");
        }
        taskRepository.delete(existingTask);
        return modelMapper.map(existingTask, AllTasksDTO.class);
    }

    @Transactional
    public TaskDTO updateTaskById(Integer taskId, String boardId, AddEditTaskDTO taskDTO) {
        Task existingTask = taskRepository.findByBoardIdAndTaskId(taskId, boardId);

        if (existingTask == null) {
            throw new NotFoundException("Task with ID " + taskId + " does not exist in board " + boardId);
        }

        taskDTO.setId(taskId);
        Task task = modelMapper.map(taskDTO, Task.class);
        Status status = statusService.getStatusById(taskDTO.getStatus());
        task.setStatus(status);

        Board board = new Board();
        board.setId(boardId);
        task.setBoard(board);
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }

    public List<AllTasksDTO> sortTasksByStatusName(String boardId, List<String> filterStatuses, String[] sortBy, String[] direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction[0]), sortBy[0]);

        if (filterStatuses == null || filterStatuses.isEmpty())
            return listMapper.mapList(taskRepository.findAllByBoardId(boardId, sort), AllTasksDTO.class, modelMapper);

        return listMapper.mapList(taskRepository.findAllByBoardIdAndStatusName(boardId, filterStatuses, sort), AllTasksDTO.class, modelMapper);
    }

    public List<AllTasksDTO> sortTasksByStatusId(String boardId, List<Integer> statusId, String[] sortBy, String[] direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction[0]), sortBy[0]);

        if (statusId == null || statusId.isEmpty())
            return listMapper.mapList(taskRepository.findAll(sort), AllTasksDTO.class, modelMapper);

        return listMapper.mapList(taskRepository.findAllByStatusIdAndBoardId(statusId, boardId, sort), AllTasksDTO.class, modelMapper);
    }
}
