package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.RequestStatusEvent;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestStatusListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private List<NotificationService> notificationServices;

    @Spy
    private List<MessageBuilder<RequestStatusEvent>> messageBuilders;

    @Spy
    @InjectMocks
    private RequestStatusListener requestStatusListener;

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
        long createdBy = 1L;
        RequestStatusEvent event = new RequestStatusEvent();
        event.setCreatedBy(createdBy);
        UserDto user = new UserDto();
        user.setId(createdBy);

        when(objectMapper.readValue(messageValue, RequestStatusEvent.class)).thenReturn(event);
        when(userServiceClient.getUser(createdBy)).thenReturn(user);
        doReturn(notificationMessage).when(requestStatusListener).getMessage(user, event);
        doNothing().when(requestStatusListener).sendNotification(user, notificationMessage);

        requestStatusListener.onMessage(message, null);

        verify(objectMapper).readValue(messageValue, RequestStatusEvent.class);
        verify(userServiceClient).getUser(createdBy);
        verify(requestStatusListener).sendNotification(user, notificationMessage);
    }

    @Test
    void onMessage_ShouldLogErrorWhenMessageCannotBeDeserialized() throws IOException {
        when(objectMapper.readValue(messageValue, RequestStatusEvent.class))
                .thenThrow(new IOException("Deserialization error"));

        requestStatusListener.onMessage(message, null);

        verify(objectMapper).readValue(messageValue, RequestStatusEvent.class);
        verify(userServiceClient, never()).getUser(anyLong());
        verify(requestStatusListener, never()).getMessage(any(UserDto.class), any(RequestStatusEvent.class));
        verify(requestStatusListener, never()).sendNotification(any(UserDto.class), anyString());
    }
}

