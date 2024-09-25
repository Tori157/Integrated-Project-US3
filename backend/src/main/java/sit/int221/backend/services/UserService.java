package sit.int221.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sit.int221.backend.user_account.User;
import sit.int221.backend.user_account.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByIds(List<String> ids) {
        return userRepository.findAllById(ids);
    }
}
