package faang.school.notificationservice.messaging;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.RecommendationReceivedEvent;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class RecommendationReceivedMessageBuilder extends MessageBuilder<RecommendationReceivedEvent> {
    public RecommendationReceivedMessageBuilder(MessageSource messageSource) {
        super(messageSource);
    }
    @Override
    public Class<RecommendationReceivedEvent> getInstance() {
        return RecommendationReceivedEvent.class;
    }

    @Override
    public String buildMessage(UserDto userDto, RecommendationReceivedEvent event) {
        Object[] args = {userDto.getUsername(), event};
        return messageSource.getMessage("recommendation.received", args, userDto.getLocale());
    }
}
