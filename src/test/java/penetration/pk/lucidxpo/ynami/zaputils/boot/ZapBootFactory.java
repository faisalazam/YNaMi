package penetration.pk.lucidxpo.ynami.zaputils.boot;

import penetration.pk.lucidxpo.ynami.zaputils.ZapInfo;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Factory to create the correct {@link ZapBoot} implementation
 * based on the given {@link ZapInfo} instance.
 */
final class ZapBootFactory {

    static ZapBoot makeZapBoot(final ZapInfo zapInfo) {
        if (zapInfo.shouldRunWithDocker()) {
            return new ZapDockerBoot(zapInfo.getPort(), zapInfo.getHost());
        }
        if (isNotBlank(zapInfo.getPath())) {
            return new ZapLocalBoot(zapInfo.getPort(), zapInfo.getHost());
        }
        return new ZapNilBoot(zapInfo.getPort(), zapInfo.getHost());
    }

    private ZapBootFactory() {
    }
}