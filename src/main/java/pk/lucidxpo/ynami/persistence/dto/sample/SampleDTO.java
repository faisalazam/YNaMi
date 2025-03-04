package pk.lucidxpo.ynami.persistence.dto.sample;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor(access = PACKAGE)
@AllArgsConstructor(access = PRIVATE)
public class SampleDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private boolean active;
    private String createdBy;
    private String lastModifiedBy;
    private Instant createdDate;
    private Instant lastModifiedDate;
}
