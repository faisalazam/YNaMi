package penetration.pk.lucidxpo.ynami.steps.config;

import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;
import penetration.pk.lucidxpo.ynami.steps.domain.NessusFalsePositive;
import penetration.pk.lucidxpo.ynami.steps.domain.ZAPFalsePositive;

import java.util.Locale;
import java.util.Map;

import static java.util.Locale.ENGLISH;

public class DataTableConfigurer implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return ENGLISH;
    }

    @Override
    public void configureTypeRegistry(final TypeRegistry registry) {
        registry.defineDataTableType(
                new DataTableType(
                        ZAPFalsePositive.class, this::getZapFalsePositive
                )
        );
        registry.defineDataTableType(
                new DataTableType(
                        NessusFalsePositive.class, this::getNessusFalsePositive
                )
        );
    }

    private NessusFalsePositive getNessusFalsePositive(final Map<String, String> entry) {
        return new NessusFalsePositive(entry.get("pluginId"), entry.get("hostname"));
    }

    private ZAPFalsePositive getZapFalsePositive(final Map<String, String> entry) {
        return new ZAPFalsePositive(entry.get("url"), entry.get("parameter"), entry.get("cweId"), entry.get("wascId"));
    }
}