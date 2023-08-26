package pk.lucidxpo.ynami.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
public class ProfileManager {
    private final Set<String> activeProfiles;

    public ProfileManager(@Value("${spring.profiles.active:}") final String activeProfiles) {
        this.activeProfiles = stream(activeProfiles.toLowerCase().split(",")).collect(Collectors.toSet());
    }

    public boolean isH2Active() {
        return isActive("H2");
    }

    @SuppressWarnings("SameParameterValue")
    private boolean isActive(final String profile) {
        return activeProfiles.contains(profile.toLowerCase());
    }
}
