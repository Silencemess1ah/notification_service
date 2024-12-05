package faang.school.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationReceivedEvent {
    private Long recommendationId;
    private Long receiverId;
    private Long authorId;
}
