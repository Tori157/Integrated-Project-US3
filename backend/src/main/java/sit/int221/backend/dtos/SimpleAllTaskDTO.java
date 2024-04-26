package sit.int221.backend.dtos;

import lombok.Data;

@Data
public class SimpleAllTaskDTO {
    private Integer id;
    private String title;
    private String assignees;
    private String status;
}
