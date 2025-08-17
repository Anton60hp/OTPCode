package ru.vgerasimov.OTPCode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vgerasimov.OTPCode.entity.User;
import ru.vgerasimov.OTPCode.entity.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    boolean existsByRole(UserRole role);

    Optional<List<User>> findByRoleNot(UserRole role);
}
