package sit.int221.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.HttpClientErrorException;
import sit.int221.backend.dtos.SimpleAllTaskDTO;
import sit.int221.backend.entities.Task;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.repositories.TaskRepository;

import java.util.List;


@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    public List<SimpleAllTaskDTO> getAllTasks() {
        return listMapper.mapList(repository.findAll(), SimpleAllTaskDTO.class, modelMapper);
    }

    public Task getTaskById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task id " + id + " dose not exist !!!") {
                }
        );
    }

    @Transactional
    public Task createNewTask(Task task) {
        return repository.save(task);
    }

    @Transactional
    public void removeTask(Integer id) {
        Task task = repository.findById(id).orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                        "Task id " + id + " dose not exist !!!")
        );
        repository.delete(task);
    }


    @Transactional
    public Task updateTask(Integer id, Task task) {
        if (task.getId() != null) {
            if (!task.getId().equals(id)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                        "Conflict id !!! (" + id + " vs " + task.getId() + ")");
            }
        }

        Task existngTask = repository.findById(id).orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                        "Taask id " + id + " dose not exist !!!"));

//        existngTask.setTitle(task.getTitle());
//        existngTask.setDescription(task.getDescription());
//        existngTask.setAssignees(task.getAssignees());
//        existngTask.setStatus(task.getStatus());

        return repository.save(existngTask);
    }
}
