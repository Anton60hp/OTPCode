package ru.vgerasimov.OTPCode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vgerasimov.OTPCode.entity.CodeStatus;
import ru.vgerasimov.OTPCode.entity.OTPCode;
import ru.vgerasimov.OTPCode.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<OTPCode, Long> {
    Optional<OTPCode> findByCode(String code);

    List<OTPCode> findByStatusAndExpirationDateBefore(CodeStatus status, LocalDateTime expirationDate);

    void deleteByUser(User user);
}
