package faang.school.notificationservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.kafka")
class KafkaProperties {

    private String bootstrapServers;
    private int interval;
    private int maxAttempts;
    private int partitionsCount;
    private int replicaCount;
    private String isolationLevel;
    private Consumer consumer;
    private Map<String, String> properties;

    private String accountOpeningTopic;

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset;
        private boolean enableAutoCommit;
    }


}
