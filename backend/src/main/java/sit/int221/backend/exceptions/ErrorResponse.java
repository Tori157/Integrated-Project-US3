package sit.int221.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final Timestamp timestamp;
    private final int status;
    private final String message;
    private final String instance;
}
