package penetration.pk.lucidxpo.ynami.hooks;

import io.cucumber.java.AfterAll;

import static penetration.pk.lucidxpo.ynami.zaputils.boot.Zap.stopZap;

public class ZapCucumberHooks {
    @AfterAll
    public static void afterAll() {
        stopZap();
    }
}
