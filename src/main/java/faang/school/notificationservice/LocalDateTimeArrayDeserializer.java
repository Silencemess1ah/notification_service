package faang.school.notificationservice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeArrayDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int[] dateTimeArray = p.readValueAs(int[].class);
        return LocalDateTime.of(
            dateTimeArray[0], dateTimeArray[1], dateTimeArray[2], // Год, месяц, день
            dateTimeArray[3], dateTimeArray[4], dateTimeArray[5], // Часы, минуты, секунды
            dateTimeArray[6]                                      // Наносекунды
        );
    }
}
