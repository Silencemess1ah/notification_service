package faang.school.notificationservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    @KafkaListener(topics = "openingAccount", groupId = "openingAccount")
    public void listen(String message) {
        log.info("Received message: {}", message);
    }
}
