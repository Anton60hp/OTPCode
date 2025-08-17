package ru.vgerasimov.OTPCode.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp_code")
public class OTPCode {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "code_id_seq")
    @SequenceGenerator(name = "code_id_seq", sequenceName = "code_id_seq", allocationSize = 1)
    private Integer id;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CodeStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private long operationId;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Transient
    private NotificationType notificationType;

}
