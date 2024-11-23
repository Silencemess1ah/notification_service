package faang.school.notificationservice.client;

import faang.school.notificationservice.dto.RecommendationRequestDto;
import faang.school.notificationservice.dto.UserDto;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @Retryable(retryFor = FeignException.class,
            maxAttemptsExpression = "${user-service.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${user-service.retry.backoff}"))
    @GetMapping("/api/v1/users/{id}/locale/contact-preference")
    UserDto getUser(@PathVariable long id);

    @Retryable(retryFor = FeignException.class,
            maxAttemptsExpression = "${user-service.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${user-service.retry.backoff}"))
    @GetMapping("/recommendation/request/{id}")
    RecommendationRequestDto getRecommendationRequest(@PathVariable long id);

}
