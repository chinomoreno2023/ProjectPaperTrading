package options.papertrading.models.alert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Alert implements Serializable {
    private final int alertId;

    @Setter
    private String stageId;

    private final String alertLevel;
    private final String alertMessage;
}