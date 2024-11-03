package faang.school.notificationservice.client;


import faang.school.notificationservice.dto.telegram.SetTelegramChatIdDto;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceWebClientTest {
    private MockWebServer mockWebServer;
    private UserServiceWebClient userServiceWebClient;

    private SetTelegramChatIdDto dto;

    private final String setTelegramChatIdUri = "/users/set-telegram-chat-id";

    @BeforeEach
    void setupMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .responseTimeout(Duration.ofMillis(2000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(2000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(2000, TimeUnit.MILLISECONDS)));

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        userServiceWebClient = new UserServiceWebClient(webClient);
        userServiceWebClient.setSetTelegramChatIdUri(setTelegramChatIdUri);

        dto = new SetTelegramChatIdDto("validToken", 123456789L);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void setTelegramChatId_ShouldCompleteSuccessfully_WhenResponseIsSuccessful() throws InterruptedException {
        String expectedBody = "{\"token\":\"validToken\",\"telegramChatId\":123456789}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value()));

        Mono<Void> result = userServiceWebClient.setTelegramChatId(dto);

        StepVerifier.create(result)
                .verifyComplete();
        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertNotNull(recordedRequest);
        assertEquals("PATCH", recordedRequest.getMethod());
        assertEquals(setTelegramChatIdUri, recordedRequest.getPath());
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"));

        String requestBody = recordedRequest.getBody().readUtf8();
        assertEquals(expectedBody, requestBody);
    }

    @Test
    void setTelegramChatId_ShouldRetry_OnError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Internal Server Error"));

        Mono<Void> result = userServiceWebClient.setTelegramChatId(dto);

        StepVerifier.create(result)
                .expectError()
                .verify();

        assertEquals(3, mockWebServer.getRequestCount());
    }
}