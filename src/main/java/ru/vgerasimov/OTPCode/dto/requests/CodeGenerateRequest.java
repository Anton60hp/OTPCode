package ru.vgerasimov.OTPCode.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.vgerasimov.OTPCode.entity.NotificationType;

@Data
@RequiredArgsConstructor
public class CodeGenerateRequest {

    private NotificationType notificationType = NotificationType.FILE;
    @NotNull
    private long operationId;
}
