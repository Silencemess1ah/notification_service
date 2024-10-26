package faang.school.notificationservice.listener.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.recommendation.RecommendationRequestEventDto;
import faang.school.notificationservice.dto.user.UserDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.messaging.recommendation.RecommendationRequestEventBuilder;
import faang.school.notificationservice.service.NotificationService;
import faang.school.notificationservice.service.telegram.TelegramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationEventListenerTest {

    private static final long ID = 1L;
    private static final String TEXT = "Text";

    @Mock
    private Message message;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TelegramService telegramService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private RecommendationRequestEventBuilder recommendationRequestEventBuilder;

    private final Map<UserDto.PreferredContact, NotificationService> notificationServiceMap = new HashMap<>();
    private final Map<Class<?>, MessageBuilder<?>> messageBuilderMap = new HashMap<>();
    private RecommendationEventListener recommendationEventListener;
    private RecommendationRequestEventDto recommendationRequestEventDto;
    private UserDto userDto;

    @BeforeEach
    void init() {
        recommendationEventListener = new RecommendationEventListener(
                objectMapper,
                userServiceClient,
                messageBuilderMap,
                notificationServiceMap
        );

        recommendationRequestEventDto = RecommendationRequestEventDto.builder()
                .id(ID)
                .receiverId(ID)
                .requesterId(ID)
                .build();

        userDto = UserDto.builder()
                .id(ID)
                .notifyPreference(UserDto.PreferredContact.TELEGRAM)
                .build();
    }

    @Test
    @DisplayName("Should notify the user of an event")
    void whenCorrectValuesThenNotifyPerson() throws IOException {
        messageBuilderMap.put(recommendationRequestEventDto.getClass(), recommendationRequestEventBuilder);
        notificationServiceMap.put(UserDto.PreferredContact.TELEGRAM, telegramService);

        when(objectMapper.readValue(message.getBody(), RecommendationRequestEventDto.class))
                .thenReturn(recommendationRequestEventDto);

        when(userServiceClient.getUser(recommendationRequestEventDto.getId()))
                .thenReturn(userDto);

        when(recommendationRequestEventBuilder.buildMessage(eq(recommendationRequestEventDto), any(Locale.class)))
                .thenReturn(TEXT);

        doNothing().when(telegramService).send(eq(userDto), anyString());

        recommendationEventListener.onMessage(message, new byte[0]);

        verify(objectMapper)
                .readValue(message.getBody(), RecommendationRequestEventDto.class);
        verify(userServiceClient)
                .getUser(recommendationRequestEventDto.getId());
        verify(telegramService)
                .send(eq(userDto), anyString());
    }

    @Test
    @DisplayName("Should throw an exception if no valid message builder is found")
    void whenNoMessageBuildersThenThrowException() throws IOException {
        when(objectMapper.readValue(message.getBody(), RecommendationRequestEventDto.class))
                .thenReturn(recommendationRequestEventDto);
        when(userServiceClient.getUser(recommendationRequestEventDto.getId()))
                .thenReturn(userDto);

        assertThrows(NoSuchElementException.class,
                () -> recommendationEventListener.onMessage(message, new byte[0]),
                "Not found message builder");
    }

    @Test
    @DisplayName("Should throw an exception if no valid preferred notification method is found")
    void whenNoPreferredNotificationMethodThenException() throws IOException {
        messageBuilderMap.put(recommendationRequestEventDto.getClass(), recommendationRequestEventBuilder);

        when(objectMapper.readValue(message.getBody(), RecommendationRequestEventDto.class))
                .thenReturn(recommendationRequestEventDto);
        when(userServiceClient.getUser(recommendationRequestEventDto.getId()))
                .thenReturn(userDto);

        assertThrows(NoSuchElementException.class,
                ()-> recommendationEventListener.onMessage(message, new byte[0]),
                "Not found notification service");
    }
}