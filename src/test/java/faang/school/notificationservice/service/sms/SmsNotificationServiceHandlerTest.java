package faang.school.notificationservice.service.sms;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.exception.MtsExolveException;
import faang.school.notificationservice.test_data.TestDataUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsNotificationServiceHandlerTest {
    @Mock
    private HttpClient client;
    @Mock
    private HttpRequest request;
    @Mock
    private HttpResponse<String> response;
    @Mock
    private SmsNotificationProperties properties;
    @InjectMocks
    private SmsNotificationServiceHandler smsNotificationServiceHandler;

    private UserDto userDto;
    private HttpResponse.BodyHandler<String> bodyHandler;


    @BeforeEach
    void setUp() {
        TestDataUser testDataUser = new TestDataUser();
        userDto = testDataUser.getUserDto();
        userDto.setPreference(UserDto.PreferredContact.PHONE);

        bodyHandler = HttpResponse.BodyHandlers.ofString();
    }

    @Nested
    class PositiveTests {
        @Test
        void testGetHttpClient_Success() {
            HttpClient result = smsNotificationServiceHandler.getHttpClient();

            assertNotNull(result);
        }

        @Test
        public void testGetHttpRequest_Success() {
            String message = "testMessage";
            String apiKey = "testApiKey";
            String senderKey = "testSenderKey";
            String receiverKey = "testReceiverKey";
            String messageKey = "messageKey";
            String authKey = "testAuthKey";
            String authPrefix = "Bearer";
            String authHeader = "Bearer " + apiKey;
            String senderNumber = "TestSenderNumber";
            URI mtsExolveURI = URI.create("https://example.com/api");

            when(properties.getApiKey()).thenReturn(apiKey);
            when(properties.getSenderNumber()).thenReturn(senderNumber);
            when(properties.getSmsURI()).thenReturn("https://example.com/api");
            when(properties.getSenderKey()).thenReturn(senderKey);
            when(properties.getReceiverKey()).thenReturn(receiverKey);
            when(properties.getMessageKey()).thenReturn(messageKey);
            when(properties.getAuthPrefix()).thenReturn(authPrefix);
            when(properties.getAuthKey()).thenReturn(authKey);

            HttpRequest httpRequest = smsNotificationServiceHandler.getHttpRequest(userDto, message);

            assertEquals(authHeader, httpRequest.headers().firstValue(authKey).orElse(null));
            assertEquals("application/json", httpRequest.headers().firstValue("Content-type").orElse(null));
            assertEquals(mtsExolveURI, httpRequest.uri());
        }

        @Test
        public void testRetryableSend_Success() throws IOException, InterruptedException {
            when(client.send(request, bodyHandler)).thenReturn(response);
            when(response.statusCode()).thenReturn(200);
            when(response.body()).thenReturn("Test");

            smsNotificationServiceHandler.retryableSend(client, request);

            assertEquals(200, response.statusCode());
            assertEquals("Test", response.body());
            verify(client, atLeastOnce()).send(request, bodyHandler);
        }
    }

    @Nested
    class NegativeTests {
        @Test
        public void testRetryableSend_getIOException_throwsRuntimeException() throws IOException, InterruptedException {
            when(client.send(request, bodyHandler)).thenThrow(new IOException());

            assertThrows(RuntimeException.class, () -> smsNotificationServiceHandler.retryableSend(client, request));
            verify(client, atLeastOnce()).send(request, bodyHandler);
        }

        @Test
        public void testRetryableSend_getInterruptedException_throwsRuntimeException() throws IOException, InterruptedException {
            when(client.send(request, bodyHandler)).thenThrow(new InterruptedException());

            assertThrows(RuntimeException.class, () -> smsNotificationServiceHandler.retryableSend(client, request));
            verify(client, atLeastOnce()).send(request, bodyHandler);
        }

        @Test
        public void testRetryableSend_failStatusCodeValidation_throwsMtsExolveException() throws IOException, InterruptedException {
            when(client.send(request, bodyHandler)).thenReturn(response);
            when(response.statusCode()).thenReturn(500);
            when(response.body()).thenReturn("someMtsExolveExceptionResponse");

            var exception = assertThrows(MtsExolveException.class,
                    () -> smsNotificationServiceHandler.retryableSend(client, request)
            );

            assertEquals("someMtsExolveExceptionResponse", exception.getMessage());
            verify(client, atLeastOnce()).send(request, bodyHandler);
        }
    }
}
