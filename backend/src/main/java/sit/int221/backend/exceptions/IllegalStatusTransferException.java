package sit.int221.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalStatusTransferException extends RuntimeException {
    public IllegalStatusTransferException(String message) {
        super(message);
    }
}
