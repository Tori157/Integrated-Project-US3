package sit.int221.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class AddEditBoardDTO {
    @NotNull
    @NotBlank
    @Size(max = 120)
    private  String name;
    private Optional<BoardOwnerDTO> ownerId;
}
