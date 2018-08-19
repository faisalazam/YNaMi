package pk.lucidxpo.ynami.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class AuditEntry {
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
    @Column //field can change from null
    private String fromValue;

    @Lob
    @Column //field can change into null
    private String toValue;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt = now();

    public AuditEntry(final String changedEntityName,
                      final String changedEntityId,
                      final String fieldChanged,
                      final String fromValue,
                      final String toValue,
                      final String changedBy) {

        this.changedEntityName = changedEntityName;
        this.changedEntityId = changedEntityId;
        this.fieldChanged = fieldChanged;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.changedBy = changedBy;
    }

    public AuditEntry(final LocalDateTime changedAt,
                      final String changedEntityName,
                      final String changedEntityId,
                      final String fieldChanged,
                      final String fromValue,
                      final String toValue,
                      final String changedBy) {
        this.changedAt = changedAt;
        this.changedEntityName = changedEntityName;
        this.changedEntityId = changedEntityId;
        this.fieldChanged = fieldChanged;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.changedBy = changedBy;
    }
}
