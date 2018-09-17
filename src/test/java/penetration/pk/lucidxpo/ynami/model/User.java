package penetration.pk.lucidxpo.ynami.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;

@Getter
@Setter
public class User {
    private List<String> roles;
    private Credentials credentials;
    private Map<String, String> recoverPasswordMap;

    public User(final Credentials credentials, final String... roles) {
        this.roles = asList(roles);
        this.credentials = credentials;
    }

    public String getDefaultRole() {
        if (roles != null && roles.size() > 0) {
            return roles.get(0);
        }
        return null;
    }

    public String getRolesAsCSV() {
        final StringBuilder res = new StringBuilder();
        range(0, roles.size()).forEach(i -> {
            res.append(roles.get(i));
            if (i < roles.size() - 1) {
                res.append(",");
            }
        });
        return res.toString();
    }

    public boolean hasRole(final String theRole) {
        return roles.stream().anyMatch(role -> role.equalsIgnoreCase(theRole));
    }

    public boolean hasRole(final List<String> theRoles) {
        return roles.stream().anyMatch(theRoles::contains);
    }
}