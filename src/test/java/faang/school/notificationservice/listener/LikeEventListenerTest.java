package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.LikeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeEventListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    @InjectMocks
    private LikeEventListener likeEventListener;

    private byte[] messageValue;
    private Message message;

    @BeforeEach
    void setUp() {
        messageValue = new byte[]{1, 2, 3};
        message = mock(Message.class);

        when(message.getBody()).thenReturn(messageValue);
    }

    @Test
    void onMessage_ShouldLogErrorWhenMessageCannotBeDeserialized() throws IOException {
        when(objectMapper.readValue(messageValue, LikeEvent.class))
                .thenThrow(new IOException("Deserialization error"));

        likeEventListener.onMessage(message, null);

        verify(objectMapper).readValue(messageValue, LikeEvent.class);
        verify(userServiceClient, never()).getUser(anyLong());
        verify(likeEventListener, never()).getMessage(any(UserDto.class), any(LikeEvent.class));
        verify(likeEventListener, never()).sendNotification(any(UserDto.class), anyString());
    }
}
