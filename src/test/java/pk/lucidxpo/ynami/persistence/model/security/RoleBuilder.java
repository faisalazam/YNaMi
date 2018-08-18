package pk.lucidxpo.ynami.persistence.model.security;

import pk.lucidxpo.ynami.persistence.model.AuditableBuilder;

public final class RoleBuilder extends AuditableBuilder<Role, RoleBuilder> {
    private RoleName name;

    private RoleBuilder() {
    }

    public static RoleBuilder aRole() {
        return new RoleBuilder();
    }

    public RoleBuilder withName(RoleName name) {
        this.name = name;
        return this;
    }

    public Role build() {
        final Role role = new Role(name);
        role.setId(id);
        role.setCreatedBy(createdBy);
        role.setCreatedDate(createdDate);
        role.setLastModifiedBy(lastModifiedBy);
        role.setLastModifiedDate(lastModifiedDate);
        return role;
    }
}