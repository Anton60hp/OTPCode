package ru.vgerasimov.OTPCode.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CodeValidationRequest {
    @NotNull
    String code;
    @NotNull
    long operationId;
}
