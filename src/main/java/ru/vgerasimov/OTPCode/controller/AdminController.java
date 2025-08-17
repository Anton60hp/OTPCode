package ru.vgerasimov.OTPCode.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vgerasimov.OTPCode.entity.CodeConfig;
import ru.vgerasimov.OTPCode.entity.User;
import ru.vgerasimov.OTPCode.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @DeleteMapping("/deleteUser/{id}")
    public boolean deleteUser(@PathVariable String id) {
        log.debug("Удаление пользователя с id: {}", id);
        if (adminService.deleteUser(id)) {
            log.debug("Удаление успешено");
            return true;
        }
        log.debug("Пользователь с id {} не найден", id);
        return false;

    }

    @GetMapping("/users")
    public List<User> showUsers() {
        log.debug("Запрос вывода всех пользователей");
        List<User> users = adminService.showUsers();
        log.debug("Получены пользователи: {}",users);
        return users;
    }

    @PutMapping("/config")
    public CodeConfig changeCodeConfig(@RequestBody CodeConfig config) {
        config.setId(1L);
        log.debug("Изменение конфигурации на: {}",config);
        return adminService.updateConfig(config);
    }
}
