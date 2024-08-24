package sit.int221.backend.securities;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sit.int221.backend.dtos.AuthenticationRequest;
import sit.int221.backend.dtos.AuthenticationResponse;
import sit.int221.backend.user_account.User;
import sit.int221.backend.user_account.UserRepository;
import sit.int221.backend.utils.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user);

        return new AuthenticationResponse(token);
    }

}
