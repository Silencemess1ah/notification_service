package faang.school.notificationservice.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestStatusEvent {

    @NotNull(message = "createdBy cannot be null")
    private Long createdBy;

    @NotNull(message = "type cannot be null")
    private RequestType type;

    private RequestStatus status;

    private String statusDescription;
}
