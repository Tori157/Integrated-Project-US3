package sit.int221.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @NotNull
    @NotBlank
    @Size(max = 50)
    @JsonProperty("userName")
    private String username;

    @NotNull
    @NotBlank
    @Size(max = 14)
    private String password;
}
