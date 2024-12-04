package faang.school.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private PreferredContact preferredContact;
    private String email;
    private String username;
    private Long id;

    public PreferredContact getPreferredContact() {
        return (preferredContact != null) ? preferredContact : PreferredContact.EMAIL;
    }

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}



