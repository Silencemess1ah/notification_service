package faang.school.notificationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MessageDeserializer {
    private final ObjectMapper objectMapper;

    public MessageDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T deserializeMessage(byte[] messageBody, Class<T> targetClass) {
        try {
            return objectMapper.readValue(messageBody, targetClass);
        } catch (IOException e) {
            log.error("Failed to deserialize message to {}", targetClass.getSimpleName(), e);
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
}
