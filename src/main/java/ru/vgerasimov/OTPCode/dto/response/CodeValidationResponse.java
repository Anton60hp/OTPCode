package ru.vgerasimov.OTPCode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodeValidationResponse {
    String code;
    boolean isValid;
}
