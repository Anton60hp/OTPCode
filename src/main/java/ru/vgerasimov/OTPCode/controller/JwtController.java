package ru.vgerasimov.OTPCode.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vgerasimov.OTPCode.dto.requests.AuthRequest;
import ru.vgerasimov.OTPCode.dto.requests.RegisterRequest;
import ru.vgerasimov.OTPCode.dto.response.JwtResponse;
import ru.vgerasimov.OTPCode.dto.response.RegisterResponse;
import ru.vgerasimov.OTPCode.entity.User;
import ru.vgerasimov.OTPCode.security.AuthenticationService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class JwtController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("Запрос на регистрацию пользователя {}", request);
        User user = authenticationService.register(request);

        ResponseEntity<RegisterResponse> ok = ResponseEntity.ok(new RegisterResponse(
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getTelegram()
        ));
        log.debug("Регистрация успешна {}", ok);
        return ok;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody AuthRequest request) {
        log.debug("Запрос авторизации для пользователя {}", request);
        ResponseEntity<JwtResponse> ok = ResponseEntity.ok(new JwtResponse(
                authenticationService.signIn(request)
        ));
        if (ok.getStatusCode().is2xxSuccessful()) {log.debug("Пользователь {} авторизован", request.getUsername());} else {
            log.debug("Не верный логин/пароль {}", request);
        }
        return ok;
    }
}
