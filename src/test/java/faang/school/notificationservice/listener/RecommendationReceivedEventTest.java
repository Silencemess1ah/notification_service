package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.RecommendationReceivedEvent;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceivedEventTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private List<NotificationService> notificationServices;

    @Spy
    private List<MessageBuilder<RecommendationReceivedEvent>> messageBuilders;

    @Spy
    @InjectMocks
    private RecommendationReceivedEventListener recommendationReceivedEventListener;

    private byte[] messageValue;
    private Message message;

    @BeforeEach
    void setUp() {
        messageValue = new byte[]{1, 2, 3};
        message = mock(Message.class);

        when(message.getBody()).thenReturn(messageValue);
    }

    @Test
    void onMessage_ShouldProcessMessageAndSendNotification() throws IOException {
        String notificationMessage = "Notification message";
        long receiverId = 1L;
        RecommendationReceivedEvent event = new RecommendationReceivedEvent();
        event.setReceiverId(receiverId);
        UserDto receiver = new UserDto();
        receiver.setId(receiverId);

        when(objectMapper.readValue(messageValue, RecommendationReceivedEvent.class)).thenReturn(event);
        when(userServiceClient.getUser(receiverId)).thenReturn(receiver);
        doReturn(notificationMessage).when(recommendationReceivedEventListener).getMessage(receiver, event);
        doNothing().when(recommendationReceivedEventListener).sendNotification(receiver, notificationMessage);

        recommendationReceivedEventListener.onMessage(message, null);

        verify(objectMapper).readValue(messageValue, RecommendationReceivedEvent.class);
        verify(userServiceClient).getUser(receiverId);
        verify(recommendationReceivedEventListener).sendNotification(receiver, notificationMessage);
    }

    @Test
    void onMessage_ShouldLogErrorWhenMessageCannotBeDeserialized() throws IOException {
        when(objectMapper.readValue(messageValue, RecommendationReceivedEvent.class))
                .thenThrow(new IOException("Deserialization error"));

        recommendationReceivedEventListener.onMessage(message, null);

        verify(objectMapper).readValue(messageValue, RecommendationReceivedEvent.class);
        verify(userServiceClient, never()).getUser(anyLong());
        verify(recommendationReceivedEventListener, never()).getMessage(any(UserDto.class), any(RecommendationReceivedEvent.class));
        verify(recommendationReceivedEventListener, never()).sendNotification(any(UserDto.class), anyString());
    }
}
