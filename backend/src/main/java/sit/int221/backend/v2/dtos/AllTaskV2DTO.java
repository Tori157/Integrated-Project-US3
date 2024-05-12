package sit.int221.backend.v2.dtos;

import lombok.Data;
import sit.int221.backend.v2.entities.Status;

@Data
public class AllTaskV2DTO {
    private Integer id;
    private String title;
    private String assignees;
    private Status status;
}
