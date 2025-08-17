package ru.vgerasimov.OTPCode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vgerasimov.OTPCode.entity.NotificationType;

@Data
@AllArgsConstructor
public class CodeGenerateResponse {
    String code;
    Long operationId;
    String username;
    NotificationType notificationType;
}
