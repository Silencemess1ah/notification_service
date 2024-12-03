package faang.school.notificationservice.config.context.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisConfigProperties(String host, int port, String sendMailTopic) {
}
