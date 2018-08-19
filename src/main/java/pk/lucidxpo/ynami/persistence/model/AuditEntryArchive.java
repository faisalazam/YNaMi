package pk.lucidxpo.ynami.persistence.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

import static pk.lucidxpo.ynami.utils.Identity.randomID;

@Data
@Entity
public class AuditEntryArchive {
    @Id
    @Column(nullable = false, updatable = false)
    private String id = randomID();

    @Column(nullable = false)
    private String changedEntityName;

    @Column(nullable = false)
    private String changedEntityId;

    @Column(nullable = false)
    private String fieldChanged;

    @Lob
    @Column
    private String fromValue;

    @Lob
    @Column
    private String toValue;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}

