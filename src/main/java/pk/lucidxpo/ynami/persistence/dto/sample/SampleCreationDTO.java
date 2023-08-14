package pk.lucidxpo.ynami.persistence.dto.sample;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(makeFinal = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PACKAGE, force = true)
public class SampleCreationDTO {
    private String firstName;
    private String lastName;
    private String address;
    private boolean active;
}
