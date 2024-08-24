package sit.int221.backend.dtos;

import lombok.Getter;
import lombok.Setter;
import sit.int221.backend.project_management.Status;

@Getter
@Setter
public class TaskDTO {
    Integer id;
    String title;
    String description;
    String assignees;
    Status status;
}



