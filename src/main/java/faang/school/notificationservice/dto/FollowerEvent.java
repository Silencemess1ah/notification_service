package faang.school.notificationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FollowerEvent {
    private long followerId;
    private long followeeId;
    private LocalDateTime eventTime;
}
