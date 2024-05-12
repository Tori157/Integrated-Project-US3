package sit.int221.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final ZonedDateTime timestamp;
    private final int status;
    private final String message;
    private final String instance;
}
