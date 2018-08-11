package pk.lucidxpo.ynami.spring;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

import static java.util.Optional.of;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return of("Anonymous");
    }
}