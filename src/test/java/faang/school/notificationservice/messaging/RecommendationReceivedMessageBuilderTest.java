package faang.school.notificationservice.messaging;

import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.RecommendationRequestDto;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.RecommendationReceivedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceivedMessageBuilderTest {

    @Mock
    private MessageSource messageSource;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private RecommendationReceivedMessageBuilder recommendationReceivedMessageBuilder;

    private final String receiverName = "John";
    private final String requesterName = "Alice";
    private final String message = "You are a great team player!";
    private final Locale locale = Locale.ENGLISH;

    private UserDto receiver;
    private UserDto requester;
    private RecommendationRequestDto recommendationRequest;
    @Mock
    private RecommendationReceivedEvent event;

    @BeforeEach
    public void setUp() {
        receiver = new UserDto();
        receiver.setUsername(receiverName);
        receiver.setLocale(locale);

        recommendationRequest = new RecommendationRequestDto();
        recommendationRequest.setMessage(message);

        requester = new UserDto();
        requester.setUsername(requesterName);
        requester.setId(1L);

        event = new RecommendationReceivedEvent();
        event.setRecommendationId(1L);
        event.setRequesterId(1L);
    }

    @Test
    public void testBuildMessage() {
        String textAddress = "recommendation.received";
        Object[] args = {receiverName, message, requesterName};
        String correctResult = "Hi John, Alice has recommended you with the following message: \"You are a great team player!\"";

        when(userServiceClient.getUser(event.getRequesterId())).thenReturn(requester);
        when(userServiceClient.getRecommendationRequest(event.getRecommendationId())).thenReturn(recommendationRequest);
        when(messageSource.getMessage(textAddress, args, locale)).thenReturn(correctResult);

        String result = recommendationReceivedMessageBuilder.buildMessage(receiver, event);

        assertEquals(correctResult, result);
    }
}