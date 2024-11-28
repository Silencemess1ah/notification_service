package faang.school.notificationservice.dto.event;

import lombok.Data;

@Data
public class RecommendationReceivedEvent {
    private Long recommendationId;
    private Long requesterId;
    private Long receiverId;
}