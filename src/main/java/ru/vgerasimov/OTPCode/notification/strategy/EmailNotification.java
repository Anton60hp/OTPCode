package ru.vgerasimov.OTPCode.notification.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vgerasimov.OTPCode.entity.NotificationType;
import ru.vgerasimov.OTPCode.entity.OTPCode;
import ru.vgerasimov.OTPCode.notification.NotificationService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class EmailNotification implements NotificationService {


    @Value("${email.from}")
    String fromEmail;
    @Value("${mail.smtp.host}")
    String host;
    @Value("${mail.smtp.port}")
    String port;
    @Value("${mail.smtp.auth}")
    String auth;
    @Value("${mail.smtp.starttls.enable}")
    String starttls;
    @Value("${email.username}")
    String emailUsername;
    @Value("${email.password}")
    String emailPassword;

    @Override
    public boolean sendCode(OTPCode code) {

        if (code.getNotificationType() != NotificationType.EMAIL) return false;

        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(code.getUser().getEmail()));
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is: " + code.getCode());

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
