package pk.lucidxpo.ynami.persistence.model.security;

import pk.lucidxpo.ynami.persistence.model.AuditableBuilder;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public final class UserBuilder extends AuditableBuilder<User, UserBuilder> {
    private Set<Role> roles = newHashSet();
    private String name = randomAlphabetic(5, 35);
    private String username = randomAlphabetic(5, 35);
    private String email = randomAlphanumeric(5) + "@" + randomAlphanumeric(5) + "." + randomAlphanumeric(3);
    private String password = randomAlphabetic(5, 95);

    private UserBuilder() {
    }

    public static UserBuilder anUser() {
        return new UserBuilder();
    }

    public UserBuilder withRole(final Role role) {
        this.roles.add(role);
        return this;
    }

    public UserBuilder withRoles(final Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UserBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withUsername(final String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withEmail(final String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPassword(final String password) {
        this.password = password;
        return this;
    }

    public User build() {
        final User user = new User(name, username, email, password);
        user.setRoles(roles);

        user.setId(id);
        user.setCreatedBy(createdBy);
        user.setCreatedDate(createdDate);
        user.setLastModifiedBy(lastModifiedBy);
        user.setLastModifiedDate(lastModifiedDate);
        return user;
    }
}
