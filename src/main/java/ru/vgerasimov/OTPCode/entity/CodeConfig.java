package ru.vgerasimov.OTPCode.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "code_config")
public class CodeConfig {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "code_lenght")
    private Integer codeLength;

    @Column(nullable = false, name="expiration_time")
    private Integer expirationTime;
}
