package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.service.telegram.ChimeraCorporationXBotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TelegramNotificationServiceTest {
    @Mock
    private ChimeraCorporationXBotService corporationXBotService;

    @InjectMocks
    private TelegramNotificationService telegramNotificationService;

    @Captor
    private ArgumentCaptor<Long> chatIdCaptor;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private UserDto userWithChatId;
    private UserDto userWithoutChatId;
    private String message;

    @BeforeEach
    void setUp() {
        userWithChatId = UserDto.builder()
                .id(1L)
                .telegramChatId(123456789L)
                .build();

        userWithoutChatId = UserDto.builder()
                .id(2L)
                .telegramChatId(null)
                .build();

        message = "Test telegram message";
    }

    @Test
    void send_userHasChatId_sendsMessage() {
        telegramNotificationService.send(userWithChatId, message);

        verify(corporationXBotService).sendNotification(chatIdCaptor.capture(), messageCaptor.capture());

        Long capturedChatId = chatIdCaptor.getValue();
        String capturedMessage = messageCaptor.getValue();

        assertEquals(userWithChatId.getTelegramChatId(), capturedChatId);
        assertEquals(message, capturedMessage);
    }

    @Test
    void send_userHasNoChatId_doesNotSendMessage() {
        telegramNotificationService.send(userWithoutChatId, message);
        verify(corporationXBotService, never()).sendNotification(anyLong(), anyString());
    }

    @Test
    void getPreferredContact_returnsTelegram() {
        assertEquals(UserDto.PreferredContact.TELEGRAM, telegramNotificationService.getPreferredContact());
    }
}
