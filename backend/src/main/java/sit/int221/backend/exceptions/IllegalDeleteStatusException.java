package sit.int221.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalDeleteStatusException extends RuntimeException {
    public IllegalDeleteStatusException(String message) {
        super(message);
    }
}
