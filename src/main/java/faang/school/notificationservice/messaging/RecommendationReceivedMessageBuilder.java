package faang.school.notificationservice.messaging;

import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.RecommendationRequestDto;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.RecommendationReceivedEvent;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class RecommendationReceivedMessageBuilder extends MessageBuilder<RecommendationReceivedEvent> {
    private final UserServiceClient userServiceClient;

    public RecommendationReceivedMessageBuilder(MessageSource messageSource, UserServiceClient userServiceClient) {
        super(messageSource);
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Class<RecommendationReceivedEvent> getInstance() {
        return RecommendationReceivedEvent.class;
    }

    @Override
    public String buildMessage(UserDto userDto, RecommendationReceivedEvent event) {
        RecommendationRequestDto recommendationRequestDto = userServiceClient.getRecommendationRequest(
                event.getRecommendationId());
        UserDto requester = userServiceClient.getUser(event.getRequesterId());

        Object[] args = {
                userDto.getUsername(),
                recommendationRequestDto.getMessage(),
                requester.getUsername()
        };

        return messageSource.getMessage("recommendation-received", args, userDto.getLocale());
    }
}
