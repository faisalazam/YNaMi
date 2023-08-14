package pk.lucidxpo.ynami.persistence.dto.sample;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(makeFinal = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PACKAGE, force = true)
public class SampleDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private boolean active;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
