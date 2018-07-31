package pk.lucidxpo.ynami.persistence.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SampleUpdateStatusDTO {
    private boolean active;
}
