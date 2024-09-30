package sit.int221.backend.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sit.int221.backend.exceptions.UserNotAuthenticatedException;
import sit.int221.backend.user_account.User;

@UtilityClass
public class SecurityUtils {
    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        return user;
    }
}
