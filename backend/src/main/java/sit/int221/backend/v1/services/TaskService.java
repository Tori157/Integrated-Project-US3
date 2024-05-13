package sit.int221.backend.v1.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.v1.dtos.AddEditTaskDTO;
import sit.int221.backend.v1.dtos.AllTaskDTO;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.v1.entities.Task;
import sit.int221.backend.v1.entities.TaskStatus;
import sit.int221.backend.v1.repositories.TaskRepository;
import sit.int221.backend.service.ListMapper;

import java.util.List;


@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    public List<AllTaskDTO> getAllTasks() {
        return listMapper.mapList(repository.findAll(Sort.by("createdOn").ascending()), AllTaskDTO.class, modelMapper);
    }


    public Task getTaskById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task id " + id + " dose not exist !!!") {

                }
        );
    }

    @Transactional
    public AddEditTaskDTO createNewTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.NO_STATUS);
        }
        if (task.getTitle() != null && task.getDescription() != null && task.getAssignees() != null) {
            task.setTitle(task.getTitle());
            task.setDescription(task.getDescription());
            task.setAssignees(task.getAssignees());
            task.setStatus(task.getStatus());
        }
        return modelMapper.map(repository.save(task), AddEditTaskDTO.class);
    }

    @Transactional
    public AllTaskDTO removeTask(Integer id) {
        Task task = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        repository.delete(task);
        return modelMapper.map(task, AllTaskDTO.class);
    }


    @Transactional
    public AddEditTaskDTO updateTask(Integer id, Task task) {

        Task existngTask = repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task id " + id + " dose not exist !!!")
        );

        existngTask.setTitle((task.getTitle()));
        existngTask.setDescription(task.getDescription());
        existngTask.setAssignees(task.getAssignees());
        existngTask.setStatus(task.getStatus());

        return modelMapper.map(repository.save(existngTask), AddEditTaskDTO.class);
    }
}
