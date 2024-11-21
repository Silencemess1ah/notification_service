package faang.school.notificationservice.messaging;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.FollowerEvent;
import faang.school.notificationservice.dto.event.RecommendationReceivedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RecommendationReceivedMessageBuilderTest {
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private RecommendationReceivedMessageBuilder recommendationReceivedMessageBuilder;

    private final String username = "testUser";
    private final Locale locale = Locale.ENGLISH;

    private UserDto user;
    private RecommendationReceivedEvent event;

    @BeforeEach
    public void setUp() {
        user = new UserDto();
        user.setUsername(username);
        user.setLocale(locale);

        event = new RecommendationReceivedEvent();
        event.setId(1L);
    }

    @Test
    public void testBuildMessage() {
        String textAddress = "recommendation.received";
        Object[] args = {username, 1L};
        String correctResult = "User testUser followed you at 15:30";
        when(messageSource.getMessage(textAddress, args, locale)).thenReturn(correctResult);

        String result = recommendationReceivedMessageBuilder.buildMessage(user, event);

        assertEquals(correctResult, result);
    }

    @Test
    public void testGetInstance() {
        assertEquals(FollowerEvent.class, recommendationReceivedMessageBuilder.getInstance());
    }
}
