package pk.lucidxpo.ynami.spring;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

import static java.util.Optional.of;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
//        TODO return the User object from here and update Auditable to have User as createdBy and modifiedBy...
//        Authentication auth = getContext().getAuthentication();
        final String username = "Crazy";//auth.getName();
        return of(username);
    }
}