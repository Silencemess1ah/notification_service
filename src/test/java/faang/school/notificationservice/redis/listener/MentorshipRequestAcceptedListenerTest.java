package faang.school.notificationservice.redis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClientMock;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.MentorshipRequestAcceptedEventMessageBuilder;
import faang.school.notificationservice.messaging.dto.MentorshipRequestAcceptedDto;
import faang.school.notificationservice.service.SmsNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestAcceptedListenerTest {

    @Mock
    private UserServiceClientMock userServiceClient;
    @Mock
    private MentorshipRequestAcceptedEventMessageBuilder messageBuilder;
    @Mock
    private SmsNotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MentorshipRequestAcceptedListener listener;

    @BeforeEach
    public void setUp() {
        listener = new MentorshipRequestAcceptedListener(userServiceClient,
                Collections.singletonList(messageBuilder),
                Collections.singletonList(notificationService),
                objectMapper);
    }

    @Test
    public void testProcessEvent() {
        long actorId = 1L;
        String receiverName = "Receiver name";
        long requestId = 123L;

        MentorshipRequestAcceptedDto event = new MentorshipRequestAcceptedDto(requestId, receiverName, actorId);

        UserDto userDto = new UserDto();
        userDto.setId(actorId);
        userDto.setUsername("testUser");
        userDto.setLocale(Locale.ENGLISH);

        when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);
        when(messageBuilder.getInstance()).thenReturn(MentorshipRequestAcceptedDto.class);
        when(messageBuilder.buildMessage(event, userDto.getLocale())).thenReturn("mentorship_request.accepted");

        listener.processEvent(event);

        ArgumentCaptor<UserDto> userDtoCaptor = ArgumentCaptor.forClass(UserDto.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificationService).send(userDtoCaptor.capture(), messageCaptor.capture());
        assertEquals(userDto, userDtoCaptor.getValue());
        assertEquals("mentorship_request.accepted", messageCaptor.getValue());
    }
}