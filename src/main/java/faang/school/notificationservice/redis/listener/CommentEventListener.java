package faang.school.notificationservice.redis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClientMock;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.messaging.dto.CommentEventDto;
import faang.school.notificationservice.redis.event.CommentEvent;
import faang.school.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CommentEventListener extends AbstractEventListener<CommentEvent> {
    public CommentEventListener(UserServiceClientMock userServiceClient,
                                List<MessageBuilder<?>> messageBuilders,
                                List<NotificationService> notificationServices,
                                ObjectMapper objectMapper) {
        super(userServiceClient, messageBuilders, notificationServices, objectMapper, CommentEvent.class);
    }

    @Override
    protected void processEvent(CommentEvent event) {
        MessageBuilder<CommentEventDto> messageBuilder =
                (MessageBuilder<CommentEventDto>) defineBuilder(CommentEventDto.class);
        UserDto postOwnerDto = getUserDto(event.getAuthorPostId());
        UserDto commentOwnerDto = getUserDto(event.getAuthorCommentId());

        CommentEventDto commentEventDto = CommentEventDto.builder()
                .commentAuthorName(commentOwnerDto.getUsername())
                .postAuthorName(postOwnerDto.getUsername())
                .content(event.getContent())
                .build();
        String message = messageBuilder.buildMessage(commentEventDto, postOwnerDto.getLocale());
        sendNotification(postOwnerDto, message);
    }
}