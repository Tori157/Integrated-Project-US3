package sit.int221.backend.dtos;

import lombok.Data;
import sit.int221.backend.project_management.Status;

@Data
public class AllTasksDTO {
    Integer id;
    String title;
    String assignees;
    Status status;
}
