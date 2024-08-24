package sit.int221.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicatedStatusException extends RuntimeException {
    public DuplicatedStatusException(String message) {
        super(message);
    }
}
