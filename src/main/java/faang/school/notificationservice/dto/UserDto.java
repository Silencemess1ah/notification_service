package faang.school.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private PreferredContact preferredContact;
    private String email;
    private String name;
    private Long id;

    public PreferredContact getPreferredContact() {
        return (preferredContact != null) ? preferredContact : PreferredContact.EMAIL;
    }

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}



