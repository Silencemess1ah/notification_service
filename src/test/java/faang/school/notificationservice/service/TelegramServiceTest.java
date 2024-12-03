package faang.school.notificationservice.service;

import faang.school.notificationservice.bot.TelegramBotImpl;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.exception.TelegramBotInitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {

    @Mock
    private TelegramBotImpl telegramBot;

    @InjectMocks
    private TelegramService telegramService;

    @Test
    @DisplayName("Send message to Telegram test")
    void testSendSuccess() throws TelegramApiException {
        UserDto user = UserDto.builder()
                .id(123456789L)
                .build();
        String message = "Test message";

        telegramService.send(user, message);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());

        SendMessage capturedMessage = argumentCaptor.getValue();
        assertEquals(user.getId(), Long.valueOf(capturedMessage.getChatId()));
        assertEquals(message, capturedMessage.getText());
    }

    @Test
    @DisplayName("Send message to Telegram test with TelegramApiException")
    void testSend_TelegramApiException() throws TelegramApiException {
        UserDto user = UserDto.builder()
                .id(123456789L)
                .build();
        String message = "Test message";

        doThrow(new TelegramApiException("API Error")).when(telegramBot).execute(any(SendMessage.class));

        TelegramBotInitException exception = assertThrows(TelegramBotInitException.class, () -> {
            telegramService.send(user, message);
        });

        assertTrue(exception.getMessage().contains("Failed to send message to Telegram"));
    }
}

