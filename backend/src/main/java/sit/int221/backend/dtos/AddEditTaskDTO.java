package sit.int221.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString
public class AddEditTaskDTO {
    Integer Id;
    @NotNull
    @NotBlank
    @Size(max = 100)
    String title;
    @Size(max = 500)
    String description;
    @Size(max = 30)
    String assignees;
    @NotNull
    Integer status;

    public void setTitle(String title) {
        this.title = StringUtils.trimToNull(title);
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimToNull(description);
    }

    public void setAssignees(String assignees) {
        this.assignees = StringUtils.trimToNull(assignees);
    }
}
