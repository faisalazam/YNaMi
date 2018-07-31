package pk.lucidxpo.ynami.persistence.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SampleCreationDTO {
    private String firstName;
    private String lastName;
    private String address;
    private boolean active;
}
