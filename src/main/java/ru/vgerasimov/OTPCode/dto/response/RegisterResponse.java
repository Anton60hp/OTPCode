package ru.vgerasimov.OTPCode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class RegisterResponse {
    @NonNull
    private String username;
    private String email;
    private String phoneNumber;
    private String Telegram;
}
