package faang.school.notificationservice.messaging;

import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.redis.event.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class PostLikeEventMessageBuilder implements MessageBuilder<PostLikeEvent> {
    private static final String POST_LIKE_CODE = "post.like";
    private final MessageSource messageSource;
    private final UserServiceClient userServiceClientReal;

    @Override
    public Class<?> getInstance() {
        return PostLikeEvent.class;
    }

    @Override
    public String buildMessage(PostLikeEvent event, Locale locale) {
        UserDto likeAuthor = userServiceClientReal.getUser(event.getLikeAuthorId());
        Object[] args = {likeAuthor.getUsername(), event.getPostId().toString()};
        return messageSource.getMessage(POST_LIKE_CODE, args, locale);
    }
}
