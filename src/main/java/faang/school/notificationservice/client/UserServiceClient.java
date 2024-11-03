package faang.school.notificationservice.client;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.telegram.SetTelegramChatIdDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDto getUser(@PathVariable long id);

    @PatchMapping("/users/set-telegram-chat-id")
    void setTelegramChatId(@Valid @RequestBody SetTelegramChatIdDto dto);
}
