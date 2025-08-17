package ru.vgerasimov.OTPCode.notification.strategy;

import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vgerasimov.OTPCode.entity.NotificationType;
import ru.vgerasimov.OTPCode.entity.OTPCode;
import ru.vgerasimov.OTPCode.notification.NotificationService;

@Component
public class SmsNotification implements NotificationService {
    @Value("${smpp.host}")
    String host;
    @Value("${smpp.port}")
    int port;
    @Value("${smpp.system_id}")
    String systemId;
    @Value("${smpp.password}")
    String password;
    @Value("${smpp.system_type}")
    String systemType;
    @Value("${smpp.source_addr}")
    String sourceAddress;

    @Override
    public boolean sendCode(OTPCode code) {
        if (code.getNotificationType() != NotificationType.SMS) return false;
        Connection connection;
        Session session;

        try {
            // 1. Установка соединения
            connection = new TCPIPConnection(host, port);
            session = new Session(connection);
            // 2. Подготовка Bind Request
            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4
            bindRequest.setAddressRange(sourceAddress);
            // 3. Выполнение привязки
            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
            }
            // 4. Отправка сообщения
            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddress);
            submitSM.setDestAddr(code.getUser().getPhone());
            submitSM.setShortMessage("Your code: " + code.getCode());

            session.submit(submitSM);
            return  true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
