package pk.lucidxpo.ynami.persistence.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

import static pk.lucidxpo.ynami.utils.Identity.randomID;

@Data
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class Auditable<U> implements Identifiable {
    //    TODO: consider replacing this class with org.springframework.data.jpa.domain.AbstractAuditable
    @Id
    @Column(nullable = false, updatable = false)
    private String id = randomID();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    //    @ManyToOne
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private U createdBy;

    @Column(nullable = false)
//    @ManyToOne
    @LastModifiedBy
    private U lastModifiedBy;
}