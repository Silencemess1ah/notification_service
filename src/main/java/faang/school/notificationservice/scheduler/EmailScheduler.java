package faang.school.notificationservice.scheduler;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailScheduler {
    private final RedisTemplate<String, UserDto> redisTemplate;
    private final EmailService emailService;

    @Scheduled(cron = "50 * * * * *")
    public void sendEmail() {
        while (true) {
            UserDto user = redisTemplate.opsForList().leftPop("emailTasks");
            if (user == null) {
                break;
            }
            String message = "Hello " + user.getUsername() + ", this is your scheduled notification!";
            emailService.send(user, message);
        }
    }
}
