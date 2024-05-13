package sit.int221.backend.v2.dtos;

import lombok.Data;
import sit.int221.backend.v2.entities.Status;

@Data
public class AddEditTaskV2DTO {
    private int id;
    private String title;
    private String description;
    private String assignees;
    private Status status;
}
