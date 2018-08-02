package pk.lucidxpo.ynami.persistence.dto.sample;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SampleUpdationDTO {
    private String firstName;
    private String lastName;
    private boolean active;
}
