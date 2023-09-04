package pk.lucidxpo.ynami.persistence.model;

import java.time.Instant;

import static pk.lucidxpo.ynami.utils.Identity.randomID;

@SuppressWarnings("unchecked")
public abstract class AuditableBuilder<E, T> {
    protected String id = randomID();
    protected String createdBy;
    protected String lastModifiedBy;
    protected Instant createdDate;
    protected Instant lastModifiedDate;

    protected AuditableBuilder() {
    }

    public abstract E build();

    public T withId(final String id) {
        this.id = id;
        return (T) this;
    }

    public T withCreatedDate(final Instant createdDate) {
        this.createdDate = createdDate;
        return (T) this;
    }

    public T withLastModifiedDate(final Instant lastModifiedDate) {
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