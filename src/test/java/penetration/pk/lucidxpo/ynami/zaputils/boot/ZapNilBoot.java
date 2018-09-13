package penetration.pk.lucidxpo.ynami.zaputils.boot;

import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Class to be used as the ZAP booter when ZAP is supposedly up and running.
 */
public class ZapNilBoot extends AbstractZapBoot {

    @Override
    String buildStartCommand(final ZapInfo zapInfo) {
        return EMPTY;
    }

    @Override
    public void startZap(final ZapInfo zapInfo) {
    }
}