package penetration.pk.lucidxpo.ynami.featureworld;

import edu.umass.cs.benchlab.har.HarEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.continuumsecurity.jsslyze.JSSLyze;
import penetration.pk.lucidxpo.ynami.model.Credentials;
import penetration.pk.lucidxpo.ynami.model.UserPassCredentials;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static lombok.AccessLevel.NONE;

/*
 * Singleton to store shared state between scenarios
 */
@Getter
@Setter
@NoArgsConstructor
public class ScenariosAwareWorld {
    private JSSLyze jSSLyze;
    private boolean spidered;
    private boolean navigated;
    private HarEntry currentHar;
    private Credentials credentials;
    private boolean sslRunCompleted;
    private List<HarEntry> recordedEntries;
    private boolean httpHeadersRecorded = false;
    private Map<String, String> sessionIds = newHashMap();

    @Getter(NONE)
    @Setter(NONE)
    private final Map<String, List<HarEntry>> methodProxyMap = newHashMap();

    @Getter(NONE)
    @Setter(NONE)
//    TODO: make it Spring component
    private static final ScenariosAwareWorld SCENARIOS_AWARE_WORLD = new ScenariosAwareWorld();

    public static ScenariosAwareWorld getInstance() {
        return SCENARIOS_AWARE_WORLD;
    }

    public synchronized Map<String, List<HarEntry>> getMethodProxyMap() {
        return methodProxyMap;
    }

    public UserPassCredentials getUserPassCredentials() {
        return (UserPassCredentials) credentials;
    }
}
