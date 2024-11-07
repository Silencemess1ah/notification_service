package faang.school.notificationservice.messaging;

import faang.school.notificationservice.config.TestContainersConfig;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.redis.event.PostLikeEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

import java.util.Locale;

@SpringBootTest
class PostPostLikeEventMessageBuilderTest extends TestContainersConfig {
    private static final String POST_LIKE_CODE = "post.like";

    @Autowired
    PostLikeEventMessageBuilder postLikeEventMessageBuilder;

    @MockBean
    UserServiceClient userServiceClient;

    @MockBean
    MessageSource messageSource;

    @Test
    void testBuildMessage() {
        PostLikeEvent postLikeEvent = new PostLikeEvent(1L, 1L, 2L);
        UserDto likeAuthor = new UserDto(1L, "username", "email", "phone", UserDto.PreferredContact.EMAIL, Locale.US);
        Object[] args = {likeAuthor.getUsername(), postLikeEvent.getPostId().toString()};
        Mockito.when(userServiceClient.getUser(1)).thenReturn(likeAuthor);
        postLikeEventMessageBuilder.buildMessage(postLikeEvent, Locale.US);
        Mockito.verify(messageSource).getMessage(POST_LIKE_CODE, args, Locale.US);
    }
}