package sit.int221.backend.securities;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.backend.exceptions.ErrorResponse;
import sit.int221.backend.user_account.User;
import sit.int221.backend.utils.JwtUtil;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equalsIgnoreCase("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HEADER_NAME);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            handleUnauthorized(
                    response,
                    "Authorization header is missing or invalid.",
                    request.getRequestURI());
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());
        String username;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (JwtException e) {
            handleJwtException(response, e, request.getRequestURI());
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateUser(request, response, jwt, username);
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtException(
            HttpServletResponse response,
            JwtException e,
            String instance)
            throws IOException {
        Map<Class<? extends JwtException>, String> errorMessages = Map.of(
                MalformedJwtException.class, "Malformed JWT token.",
                ExpiredJwtException.class, "JWT token is expired.",
                SignatureException.class, "JWT token has been tampered with or is invalid."
        );

        String message = errorMessages.getOrDefault(e.getClass(), "JWT token error.");
        handleUnauthorized(response, message, instance);
    }

    private void authenticateUser(
            HttpServletRequest request,
            HttpServletResponse response,
            String jwt,
            String username)
            throws IOException {
        User userDetails = (User) userDetailsService.loadUserByUsername(username);
        if (!jwtUtil.isTokenValid(jwt, userDetails)) {
            handleUnauthorized(response, "Invalid or expired token.", request.getRequestURI());
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void handleUnauthorized(HttpServletResponse response, String message, String instance) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                instance
        );
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}

