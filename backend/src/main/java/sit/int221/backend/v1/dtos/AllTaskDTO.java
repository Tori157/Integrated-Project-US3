package sit.int221.backend.v1.dtos;

import lombok.Data;
import sit.int221.backend.v1.entities.TaskStatus;

@Data
public class AllTaskDTO {
    private Integer id;
    private String title;
    private String assignees;
    private TaskStatus status;
}
