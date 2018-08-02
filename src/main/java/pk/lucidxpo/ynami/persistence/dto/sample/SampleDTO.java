package pk.lucidxpo.ynami.persistence.dto.sample;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SampleDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private boolean active;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
