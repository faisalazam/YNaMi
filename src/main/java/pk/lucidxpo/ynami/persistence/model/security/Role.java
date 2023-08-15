package pk.lucidxpo.ynami.persistence.model.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import pk.lucidxpo.ynami.persistence.model.Auditable;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PACKAGE;

@Data
@Entity
@Table(name = "Roles")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
// TODO: Spring Upgrade - added to fix the IT, but do I really need to add?
@NoArgsConstructor(access = PACKAGE)
public class Role extends Auditable<String> {

    @NaturalId
    @Enumerated(STRING)
    @Column(length = 60)
    private RoleName name;

    public Role(final RoleName name) {
        this.name = name;
    }
}