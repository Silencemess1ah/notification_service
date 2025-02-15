package faang.school.notificationservice.listener.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.PostServiceClient;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.comment.NewCommentEvent;
import faang.school.notificationservice.dto.post.PostDto;
import faang.school.notificationservice.dto.user.UserDto;
import faang.school.notificationservice.listener.AbstractEventListener;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class NewCommentEventListener extends AbstractEventListener<NewCommentEvent> implements MessageListener {

    private final PostServiceClient postServiceClient;

    public NewCommentEventListener(ObjectMapper objectMapper,
                                   UserServiceClient userServiceClient,
                                   PostServiceClient postServiceClient,
                                   Map<Class<?>, MessageBuilder<?>> messageBuilders,
                                   Map<UserDto.PreferredContact, NotificationService> notificationServices) {
        super(objectMapper, userServiceClient, messageBuilders, notificationServices);
        this.postServiceClient = postServiceClient;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, NewCommentEvent.class, event -> {
            log.debug("Trying to get initialPost with id - {}", event.getPostId());
            PostDto initialPost = postServiceClient.getPost(event.getPostId());
            log.debug("Got initialPost - {}", initialPost);

            log.debug("Trying to get post's author with id - {}", initialPost.getAuthorId());
            UserDto userToNotify = userServiceClient.getUser(initialPost.getAuthorId());
            log.debug("Got post's author - {}", userToNotify);

            Locale userPreferedLocale = userToNotify.getLocale() != null ? userToNotify.getLocale() : Locale.ENGLISH;
            String text = getMessage(event, userPreferedLocale);
            sendNotification(userToNotify, text);
        });
    }
}
