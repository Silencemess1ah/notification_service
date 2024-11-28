package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.RecommendationReceivedEvent;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import faang.school.notificationservice.util.MessageDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RecommendationReceivedEventListener extends AbstractEventListener<RecommendationReceivedEvent> {

    private final MessageDeserializer messageDeserializer;

    public RecommendationReceivedEventListener(ObjectMapper objectMapper,
                                               UserServiceClient userServiceClient,
                                               List<NotificationService> notificationServices,
                                               List<MessageBuilder<RecommendationReceivedEvent>> messageBuilders,
                                               MessageDeserializer messageDeserializer) {
        super(objectMapper, userServiceClient, notificationServices, messageBuilders);
        this.messageDeserializer = messageDeserializer;
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("RecommendationReceivedEventListener received a message");
        try {
            RecommendationReceivedEvent event = messageDeserializer.deserializeMessage(
                    message.getBody(),
                    RecommendationReceivedEvent.class
            );
            UserDto user = userServiceClient.getUser(event.getReceiverId());
            String text = getMessage(user, event);
            sendNotification(user, text);
            log.info("RecommendationReceivedEventListener sent a message to user {}. Requester ID: {}, Receiver ID: {}",
                    event.getReceiverId(), event.getRequesterId(), event.getReceiverId());
        } catch (Exception exception) {
            log.error("Error processing the recommendation received event", exception);
        }
    }
}
