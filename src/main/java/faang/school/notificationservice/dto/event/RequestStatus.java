package faang.school.notificationservice.dto.event;

import lombok.Getter;

@Getter
public enum RequestStatus {
    OPEN,
    PENDING,
    COMPLETED,
    CANCELLED,
}
