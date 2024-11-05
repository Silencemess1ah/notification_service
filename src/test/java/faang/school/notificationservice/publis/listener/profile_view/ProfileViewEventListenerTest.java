package faang.school.notificationservice.publis.listener.profile_view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.profile_view.ProfileViewEventDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import java.util.Collections;
import java.util.Locale;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileViewEventListenerTest {
    @InjectMocks
    private ProfileViewEventListener profileViewEventListener;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private UserServiceClient client;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MessageBuilder<ProfileViewEventDto> messageBuilder;
    @Mock
    private Message message;

    private final UserDto requestedDto = UserDto.builder()
            .id(2L)
            .username("User1")
            .email("user1@mail.com")
            .phone("8-800-555-35-35")
            .preference(UserDto.PreferredContact.EMAIL)
            .build();
    private final ProfileViewEventDto eventDto = new ProfileViewEventDto(1L, 2L);

    @BeforeEach
    void setUp() {
        profileViewEventListener = new ProfileViewEventListener(
                mapper,
                client,
                Collections.singletonList(messageBuilder),
                Collections.singletonList(notificationService));
    }

    @Test
    public void testOnMessage() throws JsonProcessingException {
        String messageBody = eventDto.toString();
        byte[] pattern = {};
        String notificationContent = "Test comment notification";

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(mapper.readValue(messageBody, ProfileViewEventDto.class)).thenReturn(eventDto);
        when(messageBuilder.getInstance()).thenReturn((Class) ProfileViewEventDto.class);
        when(messageBuilder.buildMessage(eventDto, Locale.getDefault())).thenReturn(notificationContent);
        when(client.getUser(eventDto.requestedId())).thenReturn(requestedDto);
        when(notificationService.getPreferredContact()).thenReturn(requestedDto.getPreference());

        profileViewEventListener.onMessage(message, pattern);

        verify(mapper, times(1))
                .readValue(messageBody, ProfileViewEventDto.class);
    }
}
