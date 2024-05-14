package sit.int221.backend.v2.dtos;

import lombok.Data;

@Data
public class NewTaskV2DTO {
    private int id;
    private String title;
    private String description;
    private String assignees;
    private int statusId;

    public void setTitle(String title) { this.title = title.trim(); }

    public void setDescription(String description) {
      String trimmedDescription = (description != null) ? description.trim() : null;
      this.description = (trimmedDescription != null && !trimmedDescription.isEmpty()) ? trimmedDescription : null;
    }

    public void setAssignees(String assignees) {
        String trimmedAssignees = (assignees != null) ? assignees.trim() : null;
        this.assignees = (trimmedAssignees != null && !trimmedAssignees.isEmpty()) ? trimmedAssignees : null;
    }
}
