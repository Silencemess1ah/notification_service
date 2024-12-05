package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserContactsDto;
import faang.school.notificationservice.event.RecommendationReceivedEvent;
import faang.school.notificationservice.messaging.RecommendationMessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationReceivedEventlistenerTest {

    @Mock
    private RecommendationMessageBuilder recommendationMessageBuilder;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private NotificationService emailNotificationService;

    @Mock
    private NotificationService smsNotificationService;

    @InjectMocks
    private RecommendationReceivedEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new RecommendationReceivedEventListener(
                objectMapper,
                userServiceClient,
                recommendationMessageBuilder,
                List.of(emailNotificationService, smsNotificationService)
        );
    }

    @Test
    @DisplayName("Deserialization success")
    void testDeserialization_Success() throws Exception {
        String json = "{\"recommendationId\":5,\"receiverId\":2,\"authorId\":1}";
        ObjectMapper objectMapper = new ObjectMapper();

        RecommendationReceivedEvent event = objectMapper.readValue(json, RecommendationReceivedEvent.class);

        assertNotNull(event);
        assertEquals(5L, event.getRecommendationId());
    }

    @Test
    @DisplayName("Should process event and send email notification")
    void onMessage_Success_EmailNotification() throws Exception {
        RecommendationReceivedEvent event = new RecommendationReceivedEvent(1L, 2L, 3L);
        UserContactsDto receiver = new UserContactsDto(2L, "Receiver", "receiver@example.com", "12345", UserContactsDto.PreferredContact.EMAIL);
        UserContactsDto author = new UserContactsDto(3L, "Author", "author@example.com", "67890", UserContactsDto.PreferredContact.EMAIL);
        String messageBody = "{\"recommendationId\":1,\"receiverId\":2,\"authorId\":3}";
        String generatedMessage = "Receiver, Author sent you a recommendation!";

        Message redisMessage = mock(Message.class);
        when(redisMessage.getBody()).thenReturn(messageBody.getBytes());

        when(objectMapper.readValue(any(byte[].class), eq(RecommendationReceivedEvent.class))).thenReturn(event);

        when(userServiceClient.getUserContacts(2L)).thenReturn(receiver);
        when(userServiceClient.getUserContacts(3L)).thenReturn(author);

        when(recommendationMessageBuilder.buildMessage(event, Locale.getDefault(), new Object[]{"Receiver", "Author"}))
                .thenReturn(generatedMessage);

        listener.onMessage(redisMessage, null);

        verify(objectMapper, times(1)).readValue(any(byte[].class), eq(RecommendationReceivedEvent.class));
        verify(userServiceClient, times(1)).getUserContacts(2L);
        verify(userServiceClient, times(1)).getUserContacts(3L);
        verify(recommendationMessageBuilder, times(1)).buildMessage(event, Locale.getDefault(), new Object[]{"Receiver", "Author"});
    }
}
