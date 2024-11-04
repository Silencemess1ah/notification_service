package faang.school.notificationservice.redis.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeEvent {
    private Long likeAuthorId;
    private Long postId;
    private Long postAuthorId;
}