package ru.vgerasimov.OTPCode.security;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.vgerasimov.OTPCode.dto.requests.AuthRequest;
import ru.vgerasimov.OTPCode.dto.requests.RegisterRequest;
import ru.vgerasimov.OTPCode.exception.BadRequestException;
import ru.vgerasimov.OTPCode.entity.User;
import ru.vgerasimov.OTPCode.entity.UserRole;
import ru.vgerasimov.OTPCode.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Transactional
    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {

            throw new BadRequestException("Имя пользователя уже занято");
        }

        if (registerRequest.getRole() != null &&
                registerRequest.getRole().toString().equals("ROLE_ADMIN") &&
                userRepository.existsByRole(UserRole.ROLE_ADMIN)) {
            throw new BadRequestException("Админ уже существует!");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        if (registerRequest.getRole() == null) {
            user.setRole(UserRole.ROLE_USER);
        } else {
            user.setRole(registerRequest.getRole());
        }
        user.setPhone(registerRequest.getPhone());
        user.setTelegram(registerRequest.getTelegram());


        userRepository.save(user);

        return user;
    }

    public String signIn(@Valid @RequestBody AuthRequest authRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtService.generateJWT(authentication);
    }
}
