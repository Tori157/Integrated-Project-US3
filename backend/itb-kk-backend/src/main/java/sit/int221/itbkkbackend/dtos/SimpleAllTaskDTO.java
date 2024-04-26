package sit.int221.itbkkbackend.dtos;

import lombok.Data;

@Data
public class SimpleAllTaskDTO {
    private Integer id;
    private String title;
    private String assignees;
    private String status;

    public void trimValues() {
        this.title = this.title.trim();
    }
}
