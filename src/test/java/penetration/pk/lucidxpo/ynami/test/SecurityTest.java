package penetration.pk.lucidxpo.ynami.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static penetration.pk.lucidxpo.ynami.config.Config.getInstance;
import static penetration.pk.lucidxpo.ynami.zaputils.ZapInfo.builder;
import static penetration.pk.lucidxpo.ynami.zaputils.boot.Zap.startZap;
import static penetration.pk.lucidxpo.ynami.zaputils.boot.Zap.stopZap;

@RunWith(MockitoJUnitRunner.class)
public class SecurityTest {
    private static int port;

    @BeforeClass
    public static void setup() throws Exception {
        final ZapInfo zapInfo = builder().buildToRunZap(getInstance().getZapPath());
        startZap(zapInfo);
        port = zapInfo.getPort();
    }

    @AfterClass
    public static void tearDown() {
        stopZap();
    }

    @Test
    public void shouldVerifyZapIsRunning() throws Exception {
        final String url = "http://localhost:" + port;

        final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("HEAD");
        sleep(100);
        assertEquals(HTTP_OK, conn.getResponseCode());
    }
}