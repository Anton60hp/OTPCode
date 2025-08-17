package ru.vgerasimov.OTPCode.notification.strategy;

import org.springframework.stereotype.Component;
import ru.vgerasimov.OTPCode.entity.NotificationType;
import ru.vgerasimov.OTPCode.entity.OTPCode;
import ru.vgerasimov.OTPCode.notification.NotificationService;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
public class FileNotification implements NotificationService {

    @Override
    public boolean sendCode(OTPCode code) {
        if (code.getNotificationType() != NotificationType.FILE) return false;
        try {
            String fileName = code.getUser().getUsername() +"_" + code.getOperationId()+ ".txt";
            Path path = Paths.get(fileName);
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            try (FileWriter fileWriter = new FileWriter(fileName)) {
                fileWriter.write("Time: " + LocalDateTime.now() + "\n" + "OTP Code: " + code.getCode() + "\n");
                return  true;
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения файла", e);
        }
    }
}
