package pk.lucidxpo.ynami.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Sample extends Auditable<String> {
    @Id
    @Column(nullable = false, updatable = false)
    Long id;
    @Column
    String name;
    @Column
    boolean active;

    public Sample() {
    }

    public Sample(Long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }
}
