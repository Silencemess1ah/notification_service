package faang.school.notificationservice.service.telegram;

import faang.school.notificationservice.client.UserServiceWebClient;
import faang.school.notificationservice.config.telegram.TelegramBotProperties;
import faang.school.notificationservice.dto.telegram.SetTelegramChatIdDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChimeraCorporationXBotServiceTest {

    @Mock
    private UserServiceWebClient userServiceWebClient;

    @Mock
    private TelegramBotProperties telegramBotProperties;

    @Spy
    @InjectMocks
    private ChimeraCorporationXBotService chimeraBotService;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    private final String botToken = "testBotToken";
    private final String startCommand = "/start";
    private final long chatId = 123456789L;

    @BeforeEach
    void setUp() throws TelegramApiException {
        Message dummyMessage = new Message();
        doReturn(dummyMessage).when(chimeraBotService).execute(any(SendMessage.class));
    }

    private Update createUpdate(long chatId, String messageText) {
        Update update = new Update();
        Message message = new Message();
        message.setText(messageText);
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        update.setMessage(message);
        return update;
    }

    @Test
    void onUpdateReceived_ValidToken_SendsActivationMessage() throws TelegramApiException {
        String messageText = startCommand + " " + botToken;
        Update update = createUpdate(chatId, messageText);

        when(userServiceWebClient.setTelegramChatId(any(SetTelegramChatIdDto.class)))
                .thenReturn(Mono.empty());

        when(telegramBotProperties.getStartCommand()).thenReturn(startCommand);

        chimeraBotService.onUpdateReceived(update);

        ArgumentCaptor<SetTelegramChatIdDto> dtoCaptor = ArgumentCaptor.forClass(SetTelegramChatIdDto.class);
        verify(userServiceWebClient).setTelegramChatId(dtoCaptor.capture());
        SetTelegramChatIdDto capturedDto = dtoCaptor.getValue();

        assertEquals(botToken, capturedDto.getToken());
        assertEquals(chatId, capturedDto.getTelegramChatId());

        verify(chimeraBotService).execute(sendMessageCaptor.capture());
        SendMessage sentMessage = sendMessageCaptor.getValue();

        assertEquals(String.valueOf(chatId), sentMessage.getChatId());
        assertEquals("Telegram notifications were successfully activated", sentMessage.getText());
    }

    @Test
    void onUpdateReceived_InvalidFormat_SendsInvalidFormatMessage() throws TelegramApiException {
        Update update = createUpdate(chatId, startCommand);

        chimeraBotService.onUpdateReceived(update);

        verify(chimeraBotService).execute(sendMessageCaptor.capture());
        SendMessage sentMessage = sendMessageCaptor.getValue();

        assertEquals(String.valueOf(chatId), sentMessage.getChatId());
        assertEquals("Invalid format. Please use: /start <token>", sentMessage.getText());

        verify(userServiceWebClient, never()).setTelegramChatId(any(SetTelegramChatIdDto.class));
    }

    @Test
    void processUpdate_UserServiceError_SendsErrorMessage() throws TelegramApiException {
        long chatId = 555555555L;
        String messageText = "/start " + botToken;
        Update update = createUpdate(chatId, messageText);

        when(telegramBotProperties.getStartCommand()).thenReturn(startCommand);

        when(userServiceWebClient.setTelegramChatId(any(SetTelegramChatIdDto.class)))
                .thenReturn(Mono.error(new RuntimeException("User service error")));

        chimeraBotService.onUpdateReceived(update);

        ArgumentCaptor<SetTelegramChatIdDto> dtoCaptor = ArgumentCaptor.forClass(SetTelegramChatIdDto.class);
        verify(userServiceWebClient).setTelegramChatId(dtoCaptor.capture());
        SetTelegramChatIdDto capturedDto = dtoCaptor.getValue();

        assertEquals(botToken, capturedDto.getToken());
        assertEquals(chatId, capturedDto.getTelegramChatId());

        verify(chimeraBotService).execute(sendMessageCaptor.capture());
        SendMessage errorMessage = sendMessageCaptor.getValue();

        assertEquals(String.valueOf(chatId), errorMessage.getChatId());
        assertEquals("An error occurred while processing your request. Please try again.", errorMessage.getText());
    }
}