package faang.school.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;

    public PreferredContact getPreferredContact() {
        return (preference != null) ? preference : PreferredContact.EMAIL;
    }

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}
