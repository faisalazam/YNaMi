package penetration.pk.lucidxpo.ynami.steps.config;

import io.cucumber.java.DataTableType;
import penetration.pk.lucidxpo.ynami.steps.domain.NessusFalsePositive;
import penetration.pk.lucidxpo.ynami.steps.domain.ZAPFalsePositive;

import java.util.List;
import java.util.Map;

public class DataTableConfigurer {
    @DataTableType
    public NessusFalsePositive nessusFalsePositiveTransformer(final Map<String, String> falsePositives) {
        return new NessusFalsePositive(
                falsePositives.get("pluginId"),
                falsePositives.get("hostname")
        );
    }

    @DataTableType
    public ZAPFalsePositive zapFalsePositiveTransformer(final List<String> falsePositives) {
        return new ZAPFalsePositive(
                falsePositives.get(0),
                falsePositives.get(1),
                falsePositives.get(2),
                falsePositives.get(3)
        );
    }
}
