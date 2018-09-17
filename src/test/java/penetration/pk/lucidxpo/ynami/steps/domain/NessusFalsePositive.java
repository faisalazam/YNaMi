package penetration.pk.lucidxpo.ynami.steps.domain;

import lombok.Getter;
import lombok.Setter;

import static java.lang.Integer.parseInt;

@Getter
@Setter
public class NessusFalsePositive {
    private String hostname;
    private Integer pluginId;

    public NessusFalsePositive(final String pluginId, final String hostname) {
        this.hostname = hostname;
        this.pluginId = parseInt(pluginId);
    }
}