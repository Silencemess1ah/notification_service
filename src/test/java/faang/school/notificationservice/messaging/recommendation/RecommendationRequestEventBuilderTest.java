package faang.school.notificationservice.messaging.recommendation;

import faang.school.notificationservice.dto.recommendation.RecommendationRequestEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestEventBuilderTest {

    private static final long ID = 1L;
    private static final String TEXT = "Text";

    @InjectMocks
    private RecommendationRequestEventBuilder recommendationRequestEventBuilder;

    @Mock
    private MessageSource messageSource;

    @Test
    @DisplayName("Should return recommendationRequestEventDto")
    void whenCalledMethodThenReturnCompletedEventClass() {
        assertEquals(RecommendationRequestEventDto.class, recommendationRequestEventBuilder.getInstance());
    }

    @Test
    @DisplayName("Should return expected message")
    void whenMethodThenReturnExpectedMessage() {
        RecommendationRequestEventDto eventDto = RecommendationRequestEventDto.builder()
                .id(ID)
                .build();
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage("recommendation.completed", new Object[] {ID}, locale))
                .thenReturn(TEXT);

        String message = recommendationRequestEventBuilder.buildMessage(eventDto, locale);

        assertEquals(TEXT, message);
        verify(messageSource).getMessage("recommendation.completed", new Object[] {ID}, locale);
    }
}