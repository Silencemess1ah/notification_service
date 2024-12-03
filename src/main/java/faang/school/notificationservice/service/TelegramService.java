package faang.school.notificationservice.service;

import faang.school.notificationservice.bot.TelegramBotImpl;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.exception.TelegramBotInitException;
import faang.school.notificationservice.exception.TelegramBotMessageSendException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
@Slf4j
public class TelegramService implements NotificationService {

    private final TelegramBotImpl telegramBot;

    public TelegramService(TelegramBotImpl telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    @Retryable(retryFor = TelegramBotInitException.class, backoff = @Backoff(delay = 30000, multiplier = 2))
    public void init() {
        try {
            log.debug("Trying to initializing Telegram bot");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
            log.info("Telegram bot initialized with name: {}", telegramBot.getBotUsername());
        } catch (TelegramApiException e) {
            log.error("Failed to initialize Telegram bot", e);
            throw new TelegramBotInitException("Failed to initialize Telegram bot: " + e.getMessage());
        }
    }

    @Override
    @Retryable(retryFor = TelegramBotMessageSendException.class, backoff = @Backoff(delay = 60000, multiplier = 2))
    public void send(UserDto user, String message) {
        log.debug("Sending message to user #{} in Telegram: {}", user.getId(), message);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(user.getId())
                .text(message)
                .build();
        try {
            telegramBot.execute(sendMessage);
            log.info("Message sent to user #{} in Telegram: {}", user.getId(), message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to Telegram", e);
            throw new TelegramBotMessageSendException("Failed to send message to Telegram: " + e.getMessage());
        }
    }

    @Override
    public UserDto.PreferredContact getPreferredContact() {
        return UserDto.PreferredContact.TELEGRAM;
    }
}