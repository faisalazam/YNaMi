package penetration.pk.lucidxpo.ynami.zaputils.boot;

import lombok.extern.slf4j.Slf4j;
import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import static penetration.pk.lucidxpo.ynami.zaputils.boot.ZapBootFactory.makeZapBoot;

/**
 * Utility class to help with ZAP related tasks (start and stop ZAP, run ZAP Docker image).
 */
@Slf4j
public final class Zap {
    private static ZapBoot zap;

    public static void startZap(final ZapInfo zapInfo) {
        zap = makeZapBoot(zapInfo);
        log.debug("ZAP will be started by: [{}].", zap.getClass().getSimpleName());

        zap.startZap(zapInfo);
    }

    public static void stopZap() {
        if (zap != null) {
            zap.stopZap();
        }
    }

    private Zap() {
    }
}