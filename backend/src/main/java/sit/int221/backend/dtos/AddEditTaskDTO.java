package sit.int221.backend.dtos;

import lombok.Data;
import sit.int221.backend.entities.TaskStatus;

@Data
public class AddEditTaskDTO {
    private int id;
    private String title;
    private String description;
    private String assignees;
    private TaskStatus status;
}
