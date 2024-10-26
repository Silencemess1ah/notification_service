package faang.school.notificationservice.messaging;

import faang.school.notificationservice.messaging.dto.CommentEventDto;
import faang.school.notificationservice.redis.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class CommentMessageBuilder implements MessageBuilder<CommentEventDto> {
    private static final String MESSAGE_KEY = "comment.added";

    private final MessageSource messageSource;

    @Override
    public Class<?> getInstance() {
        return CommentEvent.class;
    }

    @Override
    public String buildMessage(CommentEventDto event, Locale locale) {
        return messageSource.getMessage(
                MESSAGE_KEY,
                new Object[]{
                        event.getPostAuthorName(),
                        event.getCommentAuthorName(),
                        event.getContent()},
                locale);
    }
}
