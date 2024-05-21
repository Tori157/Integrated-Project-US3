package sit.int221.backend.v2.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.service.ListMapper;
import sit.int221.backend.v2.dtos.NewTaskV2DTO;
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
    public NewTaskV2DTO createNewTask(NewTaskV2DTO newTask) {
        Status status = statusService.getStatusById(newTask.getStatusId());
        TaskV2 task = modelMapper.map(newTask, TaskV2.class);
        task.setStatus(status);
        return modelMapper.map(taskV2Repository.save(task), NewTaskV2DTO.class);
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
    public NewTaskV2DTO updateTaskById(Integer id, NewTaskV2DTO newTask) {

        TaskV2 task = taskV2Repository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        task.setTitle(newTask.getTitle());
        task.setDescription(newTask.getDescription());
        task.setAssignees(newTask.getAssignees());
        Status status = statusService.getStatusById(newTask.getStatusId());
        task.setStatus(status);

        return modelMapper.map(taskV2Repository.save(task), NewTaskV2DTO.class);
    }

    @Transactional
    public List<AllTaskV2DTO> sortTasksByStatusName(List<String> filterStatuses, String[] sortBy, String[] direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction[0]), sortBy[0]);

        if (filterStatuses == null || filterStatuses.isEmpty())
            return listMapper.mapList(taskV2Repository.findAll(sort), AllTaskV2DTO.class, modelMapper);

        return listMapper.mapList(taskV2Repository.findAllByStatusName(filterStatuses, sort), AllTaskV2DTO.class, modelMapper);
    }

    @Transactional
    public List<AllTaskV2DTO> sortTasksByStatusId(List<Integer> statusId, String[] sortBy, String[] direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction[0]), sortBy[0]);

        if (statusId == null || statusId.isEmpty())
            return listMapper.mapList(taskV2Repository.findAll(sort), AllTaskV2DTO.class, modelMapper);

        return listMapper.mapList(taskV2Repository.findAllByStatusId(statusId, sort), AllTaskV2DTO.class, modelMapper);
    }

}

