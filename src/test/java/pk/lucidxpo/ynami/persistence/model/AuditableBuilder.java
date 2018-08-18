package pk.lucidxpo.ynami.persistence.model;

import java.time.LocalDateTime;

import static java.lang.Long.valueOf;
import static pk.lucidxpo.ynami.utils.Identity.randomInt;

@SuppressWarnings("unchecked")
public abstract class AuditableBuilder<E, T> {
    protected Long id = valueOf(randomInt());
    protected String createdBy;
    protected String lastModifiedBy;
    protected LocalDateTime createdDate;
    protected LocalDateTime lastModifiedDate;

    protected AuditableBuilder() {
    }

    public abstract E build();

    public T withId(final Long id) {
        this.id = id;
        return (T) this;
    }

    public T withCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return (T) this;
    }

    public T withLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return (T) this;
    }

    public T withCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
        return (T) this;
    }

    public T withLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return (T) this;
    }
}