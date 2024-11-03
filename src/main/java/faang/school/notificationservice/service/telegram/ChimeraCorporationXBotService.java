package faang.school.notificationservice.service.telegram;

import faang.school.notificationservice.client.UserServiceWebClient;
import faang.school.notificationservice.config.telegram.TelegramBotProperties;
import faang.school.notificationservice.dto.telegram.SetTelegramChatIdDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class ChimeraCorporationXBotService extends TelegramLongPollingBot {
    private final UserServiceWebClient userServiceWebClient;
    private final TelegramBotProperties telegramBotProperties;

    public ChimeraCorporationXBotService(TelegramBotProperties telegramBotProperties,
                                         UserServiceWebClient userServiceWebClient) {
        super(telegramBotProperties.getToken());
        this.telegramBotProperties = telegramBotProperties;
        this.userServiceWebClient = userServiceWebClient;
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String[] parts = messageText.split(" ");

            if (parts.length < 2 || !telegramBotProperties.getStartCommand().equals(parts[0])) {
                sendNotification(chatId, "Invalid format. Please use: /start <token>");
                log.warn("Received message with insufficient parts: {}", messageText);
                return;
            }

            String token = parts[1];

            log.info("Telegram token {}", token);

            SetTelegramChatIdDto setTelegramChatIdDto = new SetTelegramChatIdDto(token, chatId);

            userServiceWebClient.setTelegramChatId(setTelegramChatIdDto)
                    .then(Mono.fromRunnable(
                            () -> sendNotification(chatId, "Telegram notifications were successfully activated"))
                    )
                    .onErrorResume(error -> {
                        sendNotification(chatId, "An error occurred while processing your request. Please try again.");
                        log.error("Error processing Telegram update for chatId: {} Error: {}", chatId, error.getMessage(), error);
                        return Mono.empty();
                    })
                    .subscribe();
        }
    }

    public void sendNotification(Long chatId, String messageText) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(Long.toString(chatId));
            message.setText(messageText);
            execute(message);
            log.info("ChimeraCorporationXBot sent message '{}' to chatId {}", messageText, chatId);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId: {}", chatId, e);
        }
    }
}
