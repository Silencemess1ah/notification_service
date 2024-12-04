package faang.school.notificationservice.controller;

import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.config.telegram.MessageRequest;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.service.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/telegram")
@Validated
public class TelegramController {

    private final TelegramService telegramService;
    private final UserServiceClient userServiceClient;

    @PostMapping("/users/{userId}")
    public void sendNotification(@PathVariable long userId, @RequestBody MessageRequest messageRequest) {
        UserDto userDto = userServiceClient.getUser(userId);
        telegramService.send(userDto, messageRequest.getMessage());
    }
}
