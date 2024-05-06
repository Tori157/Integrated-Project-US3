package sit.int221.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

//public class ItemNotFoundException extends ResponseStatusException {
//    public ItemNotFoundException(String message) {
//        super(HttpStatus.NOT_FOUND, message);
//    }
//}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
