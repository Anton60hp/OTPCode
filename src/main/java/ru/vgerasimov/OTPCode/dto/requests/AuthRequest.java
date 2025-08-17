package ru.vgerasimov.OTPCode.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AuthRequest {
    @NotNull
    public String username;
    @NotNull
    public String password;
}
