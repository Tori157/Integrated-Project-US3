package sit.int221.backend.v2.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.service.ListMapper;
import sit.int221.backend.v2.dtos.AddEditTaskV2DTO;
import sit.int221.backend.v2.dtos.AllTaskV2DTO;
import sit.int221.backend.v2.entities.Status;
import sit.int221.backend.v2.entities.TaskV2;
import sit.int221.backend.v2.repositories.TaskV2Repository;

import java.util.List;

@Service
public class TaskV2Service {
    @Autowired
    private TaskV2Repository taskV2Repository;
    @Autowired
    private StatusService statusService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    public List<AllTaskV2DTO> getAllTasks() {
        return listMapper.mapList(taskV2Repository.findAll(Sort.by("createdOn").ascending()), AllTaskV2DTO.class, modelMapper);
    }


    public TaskV2 getTaskById(Integer id) {
        return taskV2Repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task id " + id + " dose not exist !!!") {

                }
        );
    }

    @Transactional
    public AddEditTaskV2DTO createNewTask(TaskV2 task) {
        if (task.getStatus() == null) {
            Status noStatus = new Status();
            noStatus.setName("NO_STATUS");
            task.setStatus(noStatus);
        }

        if (task.getTitle() != null && task.getDescription() != null && task.getAssignees() != null) {
            task.setTitle(task.getTitle());
            task.setDescription(task.getDescription());
            task.setAssignees(task.getAssignees());
            Status taskStatus = statusService.getStatusById(task.getStatus().getId());
            task.setStatus(taskStatus);
        }
        return modelMapper.map(taskV2Repository.save(task), AddEditTaskV2DTO.class);
    }

    @Transactional
    public AllTaskV2DTO removeTaskById(Integer id) {
        TaskV2 task = taskV2Repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        taskV2Repository.delete(task);
        return modelMapper.map(task, AllTaskV2DTO.class);
    }


    @Transactional
    public AddEditTaskV2DTO updateTaskById(Integer id, TaskV2 task) {

        TaskV2 existngTask = taskV2Repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task id " + id + " dose not exist !!!")
        );

        existngTask.setTitle((task.getTitle()));
        existngTask.setDescription(task.getDescription());
        existngTask.setAssignees(task.getAssignees());
        Status taskStatus = statusService.getStatusById(task.getStatus().getId());
        existngTask.setStatus(taskStatus);

        return modelMapper.map(taskV2Repository.save(existngTask), AddEditTaskV2DTO.class);
    }
}

