package faang.school.notificationservice.client;

import faang.school.notificationservice.dto.telegram.SetTelegramChatIdDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceWebClient {
    private final WebClient userServiceWebClientConfig;

    @Setter
    @Value("${user-service.set-telegram-chat-id-uri}")
    private String setTelegramChatIdUri;

    public Mono<Void> setTelegramChatId(SetTelegramChatIdDto dto) {
        return userServiceWebClientConfig.patch()
                .uri(setTelegramChatIdUri)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(
                        Retry.fixedDelay(2, Duration.ofSeconds(1))
                                .doBeforeRetry(retrySignal ->
                                        log.warn("Retrying setTelegramChatId due to error: {}", retrySignal.failure().getMessage())
                                )
                )
                .doOnError(
                        error -> log.error("Error setting Telegram chat ID after retries: {}", error.getMessage(), error));
    }
}
