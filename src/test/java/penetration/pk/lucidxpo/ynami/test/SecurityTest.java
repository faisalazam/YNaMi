package penetration.pk.lucidxpo.ynami.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import penetration.pk.lucidxpo.ynami.config.Config;

import java.io.IOException;

import static penetration.pk.lucidxpo.ynami.scanners.ZapManager.getInstance;

@RunWith(MockitoJUnitRunner.class)
public class SecurityTest {
    @BeforeClass
    public static void setup() throws Exception {
        getInstance().startZAP(Config.getInstance().getZapPath());
    }

    @AfterClass
    public static void tearDown() throws IOException {
        getInstance().stopZap();
    }

    @Test
    public void doNothing() {
    }
}