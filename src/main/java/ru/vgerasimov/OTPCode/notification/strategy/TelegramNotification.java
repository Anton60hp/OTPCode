package ru.vgerasimov.OTPCode.notification.strategy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vgerasimov.OTPCode.entity.NotificationType;
import ru.vgerasimov.OTPCode.entity.OTPCode;
import ru.vgerasimov.OTPCode.notification.NotificationService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class TelegramNotification implements NotificationService {

    @Value("${telegram.bot_token}")
    private String telegramBotToken;



    @Override
    public boolean sendCode(OTPCode code) {
        String telegramApiUrl = String.format("https://api.telegram.org/bot%s/sendMessage", telegramBotToken);
        if (code.getNotificationType() != NotificationType.TELEGRAM) return false;
        String message = String.format(code.getUser().getTelegram() + ", your confirmation code is: %s", code.getCode());
        String url = String.format("%s?chat_id=%s&text=%s",
                telegramApiUrl,
                getTelegramChatId(code.getUser().getTelegram()),
                urlEncode(message));

        sendTelegramRequest(url);
        return true;
    }

    private void sendTelegramRequest(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("Telegram API error. Status code: " + statusCode);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error sending Telegram message: {}", e);
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String getTelegramChatId(String telegramUser) {
        String url = "https://api.telegram.org/bot" + telegramBotToken + "/getUpdates";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = new String(response.getEntity().getContent().readAllBytes());
                    JsonArray results = JsonParser.parseString(responseBody).getAsJsonObject()
                            .getAsJsonArray("result");
                    for (JsonElement jsonObject : results) {

                        JsonObject chatObj = jsonObject.getAsJsonObject()
                                .getAsJsonObject("message")
                                .getAsJsonObject("chat");
                        if (chatObj
                                .get("username")
                                .getAsString()
                                .equals(telegramUser)) {
                            return chatObj.get("id").toString();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при получении chatId: {}", e);
        }
        return null;
    }
}

