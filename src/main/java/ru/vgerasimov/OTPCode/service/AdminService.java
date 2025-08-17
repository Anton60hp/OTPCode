package ru.vgerasimov.OTPCode.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vgerasimov.OTPCode.entity.CodeConfig;
import ru.vgerasimov.OTPCode.entity.User;
import ru.vgerasimov.OTPCode.entity.UserRole;
import ru.vgerasimov.OTPCode.repository.CodeRepository;
import ru.vgerasimov.OTPCode.repository.ConfigRepository;
import ru.vgerasimov.OTPCode.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConfigRepository configRepository;
    @Autowired
    CodeRepository codeRepository;

    @Transactional
    public CodeConfig updateConfig(CodeConfig config) {

        return configRepository.save(config);
    }

    @Transactional
    public boolean deleteUser(String id) {
        Optional<User> user = userRepository.findById(Integer.valueOf(id));
        if (user.isPresent()) {
            codeRepository.deleteByUser(user.get());
            userRepository.deleteById(Integer.valueOf(id));
            return true;
        }
        return false;
    }


    public List<User> showUsers() {
        return userRepository.findByRoleNot(UserRole.ROLE_ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("Нет пользователей"));
    }
}
