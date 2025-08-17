package ru.vgerasimov.OTPCode.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vgerasimov.OTPCode.dto.requests.CodeValidationRequest;
import ru.vgerasimov.OTPCode.dto.requests.CodeGenerateRequest;
import ru.vgerasimov.OTPCode.dto.response.CodeGenerateResponse;
import ru.vgerasimov.OTPCode.dto.response.CodeValidationResponse;
import ru.vgerasimov.OTPCode.entity.NotificationType;
import ru.vgerasimov.OTPCode.entity.OTPCode;
import ru.vgerasimov.OTPCode.notification.NotificationFacade;
import ru.vgerasimov.OTPCode.service.CodeService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class CodeController {

    private final CodeService codeService;

    private final NotificationFacade notificationFacade;

    @PostMapping("/generate")
    public ResponseEntity<CodeGenerateResponse> generateOTPCode(@Valid @RequestBody CodeGenerateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Запрос на генерацию ОТП: {}, от {}", request, username);
        OTPCode code = codeService.getCode(
                username,
                request.getOperationId(),
                request.getNotificationType()
        );
        NotificationType usedType =  notificationFacade.sendCode(code);
        log.debug("Использованный способ передачи кода: {}", usedType);
        ResponseEntity<CodeGenerateResponse> ok = ResponseEntity.ok(new CodeGenerateResponse(
                code.getCode(),
                code.getOperationId(),
                code.getUser().getUsername(),
                usedType
        ));
        log.debug("Результат запроса генерации: {}", ok);
        return ok;
    }

    @PostMapping("/validate")
    public ResponseEntity<CodeValidationResponse> validateOTPCode(@Valid @RequestBody CodeValidationRequest request) {
        log.debug("Запрос на валидацию ОТП: {}, пользователем: {}", request, SecurityContextHolder.getContext().getAuthentication().getName());
        ResponseEntity<CodeValidationResponse> ok = ResponseEntity.ok(new CodeValidationResponse(
                request.getCode(),
                codeService.validateCode(request.getCode(), request.getOperationId()))
        );
        log.debug("Результат запроса валидации: {}", ok);

        return ok;
    }

}
