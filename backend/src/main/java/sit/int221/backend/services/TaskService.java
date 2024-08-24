package sit.int221.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.backend.dtos.AddEditTaskDTO;
import sit.int221.backend.dtos.AllTasksDTO;
import sit.int221.backend.dtos.TaskDTO;
import sit.int221.backend.exceptions.StatusWithIdNotFoundException;
import sit.int221.backend.utils.ListMapper;
import sit.int221.backend.project_management.Status;
import sit.int221.backend.project_management.Task;
import sit.int221.backend.project_management.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private TaskRepository taskRepository;
    private StatusService statusService;
    private ModelMapper modelMapper;
    private ListMapper listMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, StatusService statusService, ModelMapper modelMapper, ListMapper listMapper) {
        this.taskRepository = taskRepository;
        this.statusService = statusService;
        this.modelMapper = modelMapper;
        this.listMapper = listMapper;
    }


    public List<AllTasksDTO> getAllTasks(String field, String order, List<String> filterStatuses) {
        List<Task> tasks;
        Sort sort = Sort.by(Sort.Direction.fromString(order), field);

        if (filterStatuses == null) {
            tasks = taskRepository.findAll(sort);
        } else {
            tasks = taskRepository.findAllByStatusName(filterStatuses, sort);
        }
        return listMapper.mapList(taskRepository.findAll(), AllTasksDTO.class, modelMapper);
//        return listMapper.mapList(taskRepository.findAll(Sort.by("createdOn").ascending()), AllTasksDTO.class, modelMapper);
    }


    public Task getTaskById(Integer id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task " + id + " does not exist !!!")
        );
    }

    @Transactional
    public TaskDTO createTask(AddEditTaskDTO newTask) {
        Status status = statusService.getStatusById(newTask.getStatusId());
        Task task = modelMapper.map(newTask, Task.class);
        task.setStatus(status);
        Task createdTask = taskRepository.save(task);
        return modelMapper.map(taskRepository.save(task), TaskDTO.class);
    }

    @Transactional
    public AllTasksDTO removeTaskById(Integer id) {
        Task existingTask = taskRepository.findById(id).orElseThrow(
                () -> new StatusWithIdNotFoundException("does not exist")
        );
        taskRepository.delete(existingTask);
        return modelMapper.map(existingTask, AllTasksDTO.class);
    }

    @Transactional
    public TaskDTO updateTaskById(Integer id, AddEditTaskDTO newTask) {

        Task existingTask = taskRepository.findById(id).orElseThrow(
                () -> new StatusWithIdNotFoundException("does not exist")
        );

        newTask.setId(id);
        Task task = modelMapper.map(newTask, Task.class);
        Task updatedTask = taskRepository.save(task);
        Status status = statusService.getStatusById(newTask.getStatusId());
        task.setStatus(status);

        return modelMapper.map(updatedTask, TaskDTO.class);
    }

    @Transactional
    public List<AllTasksDTO> sortTasksByStatusName(List<String> filterStatuses, String[] sortBy, String[] direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction[0]), sortBy[0]);

        if (filterStatuses == null || filterStatuses.isEmpty())
            return listMapper.mapList(taskRepository.findAll(sort), AllTasksDTO.class, modelMapper);

        return listMapper.mapList(taskRepository.findAllByStatusName(filterStatuses, sort), AllTasksDTO.class, modelMapper);
    }

    @Transactional
    public List<AllTasksDTO> sortTasksByStatusId(List<Integer> statusId, String[] sortBy, String[] direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction[0]), sortBy[0]);

        if (statusId == null || statusId.isEmpty())
            return listMapper.mapList(taskRepository.findAll(sort), AllTasksDTO.class, modelMapper);

        return listMapper.mapList(taskRepository.findAllByStatusId(statusId, sort), AllTasksDTO.class, modelMapper);
    }

    public boolean hasTaskWithStatus(Integer statusId) {
        return taskRepository.existsByStatusId(statusId);
    }

}
