package faang.school.notificationservice.redis.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.config.TestContainersConfig;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.client.UserServiceClientMock;
import faang.school.notificationservice.config.TestListenerConfig;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.PostLikeEventMessageBuilder;
import faang.school.notificationservice.redis.event.PostLikeEvent;
import faang.school.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestListenerConfig.class)
class PostPostLikeEventMessageListenerTest extends TestContainersConfig {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Topic postLikeTopic;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceClient userServiceClientReal;

    @MockBean
    private UserServiceClientMock userServiceClientMock;

    @MockBean
    private NotificationService emailService;

    @SpyBean
    private PostLikeEventMessageBuilder postLikeEventMessageBuilder;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<UserDto> userCaptor;

    @Test
    public void testLikeEventListener() throws JsonProcessingException, InterruptedException {
        PostLikeEvent postLikeEvent = new PostLikeEvent(1L, 1L, 2L);
        UserDto likeAuthor = new UserDto(1L, "username", "email", "phone", UserDto.PreferredContact.EMAIL, Locale.US);
        UserDto postAuthor = new UserDto(2L, "postAuthor", "email", "phone", UserDto.PreferredContact.EMAIL, Locale.US);
        String event = "event";

        when(userServiceClientReal.getUser(1)).thenReturn(likeAuthor);
        when(emailService.getPreferredContact()).thenReturn(UserDto.PreferredContact.EMAIL);
        when(userServiceClientMock.getUser(postLikeEvent.getPostAuthorId())).thenReturn(postAuthor);
        when(postLikeEventMessageBuilder.buildMessage(postLikeEvent, Locale.US)).thenReturn(event);

        String json = objectMapper.writeValueAsString(postLikeEvent);
        redisTemplate.convertAndSend(postLikeTopic.getTopic(), json);

        Thread.sleep(2000);

        verify(emailService).send(userCaptor.capture(), stringCaptor.capture());

        assertEquals(postAuthor.getUsername(), userCaptor.getValue().getUsername());
        assertEquals(postAuthor.getEmail(), userCaptor.getValue().getEmail());
        assertEquals(event, stringCaptor.getValue());
    }
}