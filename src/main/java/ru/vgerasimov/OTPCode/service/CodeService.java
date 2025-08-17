package ru.vgerasimov.OTPCode.service;

import  lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vgerasimov.OTPCode.entity.CodeStatus;
import ru.vgerasimov.OTPCode.entity.NotificationType;
import ru.vgerasimov.OTPCode.entity.OTPCode;
import ru.vgerasimov.OTPCode.entity.User;
import ru.vgerasimov.OTPCode.notification.NotificationFacade;
import ru.vgerasimov.OTPCode.repository.CodeRepository;
import ru.vgerasimov.OTPCode.repository.ConfigRepository;
import ru.vgerasimov.OTPCode.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final UserRepository userRepository;
    private final ConfigRepository configRepository;
    private final CodeRepository codeRepository;

    int rngNumber;

    public OTPCode getCode(String userId, Long operationId, NotificationType notificationType) {
        String code = "";
        User user = userRepository.findById(Integer.valueOf(userId)).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        int length = configRepository.findById(1).getCodeLength();
        int TTLSec = configRepository.findById(1).getExpirationTime();
        for (int i = 1; i <= length; i++) {
            rngNumber = new Random().nextInt(9);
            code = code.concat(Integer.toString(rngNumber));
        }
        OTPCode otpCode = new OTPCode();
        otpCode.setCode(code);
        otpCode.setUser(user);
        otpCode.setStatus(CodeStatus.ACTIVE);
        otpCode.setOperationId(operationId);
        otpCode.setCreationDate(LocalDateTime.now());
        otpCode.setExpirationDate(LocalDateTime.now().plusSeconds(TTLSec));
        otpCode.setNotificationType(notificationType);


        codeRepository.save(otpCode);

        return otpCode;
    }

    @Transactional
    public boolean validateCode(String codeString, long operationId) {
        OTPCode code = codeRepository.findByCode(codeString).orElse(null);

        if (code == null || code.getStatus() != CodeStatus.ACTIVE || code.getOperationId() != operationId) {
            return false;
        }
        if (LocalDateTime.now().isAfter(code.getExpirationDate())) {
            code.setStatus(CodeStatus.EXPIRED);
            codeRepository.save(code);
            return false;
        }

        code.setStatus(CodeStatus.USED);
        codeRepository.save(code);
        return true;
    }

    @Scheduled(fixedRate = 5000)
    public void expireCodes() {

        List<OTPCode> codes = codeRepository.findByStatusAndExpirationDateBefore(
                CodeStatus.ACTIVE, LocalDateTime.now()
        );

        codes.forEach(code -> code.setStatus(CodeStatus.EXPIRED));
        codeRepository.saveAll(codes);
    }
}
