package penetration.pk.lucidxpo.ynami.zaputils.boot;

import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;
import penetration.pk.lucidxpo.ynami.zaputils.exception.ZapInitializationTimeoutException;

interface ZapBoot {
    /**
     * Starts ZAP.
     * <p>
     * It should throw {@link ZapInitializationTimeoutException}
     * in case ZAP is not started before a timeout, defined by {@link ZapInfo#getInitializationTimeoutInMillis}
     * (the default value is {@link ZapInfo#DEFAULT_INITIALIZATION_TIMEOUT_IN_MILLIS}).
     *
     * @param zapInfo an object with all the information needed to start ZAP.
     */
    @SuppressWarnings("JavadocReference")
    void startZap(ZapInfo zapInfo);

    void stopZap();
}