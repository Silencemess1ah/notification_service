package faang.school.notificationservice.messaging.recommendation;

import faang.school.notificationservice.dto.recommendation.RecommendationRequestEventDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class RecommendationRequestEventBuilder implements MessageBuilder<RecommendationRequestEventDto> {

    private final MessageSource messageSource;

    @Override
    public Class<?> getInstance() {
        return RecommendationRequestEventDto.class;
    }

    @Override
    public String buildMessage(RecommendationRequestEventDto event, Locale locale) {
        return messageSource.getMessage("recommendation.completed", new Object[] {event.getId()}, locale);
    }
}
