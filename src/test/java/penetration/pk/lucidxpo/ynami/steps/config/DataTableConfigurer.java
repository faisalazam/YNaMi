package penetration.pk.lucidxpo.ynami.steps.config;

import penetration.pk.lucidxpo.ynami.steps.domain.NessusFalsePositive;
import penetration.pk.lucidxpo.ynami.steps.domain.ZAPFalsePositive;

import java.util.Map;

public class DataTableConfigurer {
    // TODO Cucumber Upgrade: check if this new code works
    public DataTableConfigurer() {
//        TODO: fix me after removal of cucumber-java8
//        DataTableType(this::getZapFalsePositive);
//        DataTableType(this::getNessusFalsePositive);
    }

    private NessusFalsePositive getNessusFalsePositive(final Map<String, String> entry) {
        return new NessusFalsePositive(entry.get("pluginId"), entry.get("hostname"));
    }

    private ZAPFalsePositive getZapFalsePositive(final Map<String, String> entry) {
        return new ZAPFalsePositive(entry.get("url"), entry.get("parameter"), entry.get("cweId"), entry.get("wascId"));
    }
}
