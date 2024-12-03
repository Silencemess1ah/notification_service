package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            UserDto dto = objectMapper.readValue(message.getBody(), UserDto.class);
            log.info("Received message: {}", dto);
            UserDto userDto = userServiceClient.getUser(dto.getId());
            log.info("User: {}", userDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
