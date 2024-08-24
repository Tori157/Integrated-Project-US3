package sit.int221.backend.utils;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.dtos.AddEditTaskDTO;
import sit.int221.backend.dtos.AllTasksDTO;
import sit.int221.backend.exceptions.StatusWithIdNotFoundException;
import sit.int221.backend.project_management.Status;
import sit.int221.backend.project_management.Task;
import sit.int221.backend.project_management.TaskRepository;
import sit.int221.backend.services.StatusService;

import java.util.List;
import java.util.stream.Collectors;

public class ListMapper {

    private static final ListMapper listMapper = new ListMapper();

    private static ModelMapper modelMapper = new ModelMapper();

    private ListMapper() {
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return source.stream().map(entity -> modelMapper.map(entity, targetClass))
                .collect(Collectors.toList());
    }


    public static ListMapper getInstance() {
        return listMapper;
    }

    @Service
    public static class TaskService {
        @Autowired
        private TaskRepository taskRepository;
        @Autowired
        private StatusService statusService;
        @Autowired
        ModelMapper modelMapper;
        @Autowired
        ListMapper listMapper;

        public List<AllTasksDTO> getAllTasks() {
            return listMapper.mapList(taskRepository.findAll(Sort.by("createdOn").ascending()), AllTasksDTO.class, modelMapper);
        }


        public Task getTaskById(Integer id) {
            return taskRepository.findById(id).orElseThrow(
                    () -> new StatusWithIdNotFoundException("Task id " + id + " dose not exist !!!") {

                    }
            );
        }

        @Transactional
        public AddEditTaskDTO createNewTask(AddEditTaskDTO newTask) {
            Status status = statusService.getStatusById(newTask.getStatusId());
            Task task = modelMapper.map(newTask, Task.class);
            task.setStatus(status);
            return modelMapper.map(taskRepository.save(task), AddEditTaskDTO.class);
        }

        @Transactional
        public AllTasksDTO removeTaskById(Integer id) {
            Task task = taskRepository.findById(id).orElseThrow(
                    () -> new StatusWithIdNotFoundException("NOT FOUND")
            );
            taskRepository.delete(task);
            return modelMapper.map(task, AllTasksDTO.class);
        }

        @Transactional
        public AddEditTaskDTO updateTaskById(Integer id, AddEditTaskDTO newTask) {

            Task task = taskRepository.findById(id).orElseThrow(
                    () -> new StatusWithIdNotFoundException("NOT FOUND")
            );
            task.setTitle(newTask.getTitle());
            task.setDescription(newTask.getDescription());
            task.setAssignees(newTask.getAssignees());
            Status status = statusService.getStatusById(newTask.getStatusId());
            task.setStatus(status);

            return modelMapper.map(taskRepository.save(task), AddEditTaskDTO.class);
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

    }
}
