package faang.school.notificationservice.service.telegram;

import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.entity.TelegramUser;
import faang.school.notificationservice.repository.TelegramUserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationBot extends TelegramLongPollingBot {
    private static final String BOT_NAME = "TelegramNotificationBot";
    private final UserServiceClient userServiceClient;
    private final TelegramUserRepository telegramUserRepository;

    @Override
    @Transactional
    @Retryable(retryFor = {FeignException.class, TelegramApiException.class},
            maxAttempts = 5, backoff = @Backoff(delay = 500))
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                String response = "Для получения оповещений предоставьте,пожалуйста, ваш id.";
                sendMessage(chatId, response);
            } else {
                subscribeToNotifications(messageText, chatId);
            }
        }
    }

    @Retryable(retryFor = TelegramApiException.class, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public void sendNotification(long chatId, String notification) {
        sendMessage(chatId, notification);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return "7063955480:AAFT1nYPcT1SAv8zVQj0Mz-3i2ln7PaegQg";
    }

    private void subscribeToNotifications(String messageText, long chatId) {
        try {
            long userId = Long.parseLong(messageText);
            processUserId(chatId, userId);
        } catch (NumberFormatException e) {
            String response = "Предоставьте корректный id";
            sendMessage(chatId, response);
        }
    }

    private void processUserId(long chatId, long userId) {
        if (userServiceClient.isUserExists(userId)) {
            String response = "Вы подписались на получение оповещений";
            TelegramUser telegramUser = TelegramUser.builder().chatId(chatId).userId(userId).build();
            if (!telegramUserRepository.existsByChatId(chatId)) {
                telegramUserRepository.save(telegramUser);
                sendMessage(chatId, response);
            } else {
                String response2 = "Вы уже подписались на получение оповещений";
                sendMessage(chatId, response2);
            }
        } else {
            String response = "Ваш аккаунт не найден";
            sendMessage(chatId, response);
        }
    }


    private void sendMessage(long chatId, String response) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(response).build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Message has not sent {}", String.valueOf(e));
        }
    }
}
