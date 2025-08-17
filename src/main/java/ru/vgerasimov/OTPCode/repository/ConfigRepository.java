package ru.vgerasimov.OTPCode.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vgerasimov.OTPCode.entity.CodeConfig;

@Repository
public interface ConfigRepository extends JpaRepository<CodeConfig, Long> {
    CodeConfig findById(long id);
}
