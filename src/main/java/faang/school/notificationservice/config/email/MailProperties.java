package faang.school.notificationservice.config.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(
        String host,
        int port,
        String username,
        String password,
        String protocol,
        boolean debug) {
}
