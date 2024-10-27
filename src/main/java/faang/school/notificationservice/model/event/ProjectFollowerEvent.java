package faang.school.notificationservice.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProjectFollowerEvent {
    private Long followerId;
    private Long projectId;
    private Long creatorId;
}