package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.RecommendationRequestDto;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.FollowerEvent;
import faang.school.notificationservice.dto.event.RecommendationReceivedEvent;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class RecommendationReceivedEventListener extends AbstractEventListener<RecommendationReceivedEvent> {
    public RecommendationReceivedEventListener(ObjectMapper objectMapper,
                                               UserServiceClient userServiceClient,
                                               List<NotificationService> notificationServices,
                                               List<MessageBuilder<RecommendationReceivedEvent>> messageBuilders) {
        super(objectMapper, userServiceClient, notificationServices, messageBuilders);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("RecommendationReceivedEventListener received a message");
            RecommendationReceivedEvent event = objectMapper.readValue(
                    message.getBody(),
                    RecommendationReceivedEvent.class);
            UserDto user = userServiceClient.getUser(event.getReceiverId());
            RecommendationRequestDto recommendationRequestDto = userServiceClient.getRecommendationRequest(
                    event.getId());
            String text = getMessage(user, event);
            sendNotification(user, text);
            log.info("RecommendationReceivedEventListener sent a message to user {}. Requester ID: {}, Receiver ID: {}",
                    event.getReceiverId(), event.getRequesterId(), event.getReceiverId());
        } catch (IOException exception) {
            log.error("Failed to deserialize recommendation received event", exception);
        }
    }
}
