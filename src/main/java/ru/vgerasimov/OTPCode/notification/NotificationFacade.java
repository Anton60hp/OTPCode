package ru.vgerasimov.OTPCode.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vgerasimov.OTPCode.entity.NotificationType;
import ru.vgerasimov.OTPCode.entity.OTPCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationFacade {
    private final List<NotificationService> notificationServices;

    public NotificationType sendCode(OTPCode code) {
        for (NotificationService notificationService : notificationServices) {
            if (notificationService.sendCode(code)) {
                return code.getNotificationType();
            }
        }
        return null;
    }

}
