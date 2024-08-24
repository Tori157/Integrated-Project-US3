package sit.int221.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class AddEditStatusDTO {
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String name;
    @Size(max = 200)
    private String description;

    public void setName(String name) {
        this.name = StringUtils.trimToNull(name);
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimToNull(description);
    }
}

