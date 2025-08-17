package ru.vgerasimov.OTPCode.notification;

import ru.vgerasimov.OTPCode.entity.OTPCode;

public interface NotificationService {
    boolean sendCode(OTPCode code);
}
