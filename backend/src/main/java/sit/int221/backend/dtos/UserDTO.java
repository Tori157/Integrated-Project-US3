package sit.int221.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDTO {
    private String oid;
    private String name;
    private String username;
    @JsonProperty("access_token")
    private String accessToken;
}
