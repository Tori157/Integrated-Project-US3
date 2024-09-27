package sit.int221.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.int221.backend.project_management.Visibility;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private String id;
    @NotNull
    @NotBlank
    @Size(max = 120)
    private String name;
    private Optional<BoardOwnerDTO> owner;
    private Visibility visibility;
}