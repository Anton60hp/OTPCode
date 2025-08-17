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
import ru.vgerasimov.OTPCode.repository.ConfigRepository;
import ru.vgerasimov.OTPCode.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConfigRepository configRepository;

    @Transactional
    public CodeConfig updateConfig(CodeConfig config) {

        return configRepository.save(config);
    }


    public boolean deleteUser(String id) {

        if (userRepository.existsById(Integer.valueOf(id))) {
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
