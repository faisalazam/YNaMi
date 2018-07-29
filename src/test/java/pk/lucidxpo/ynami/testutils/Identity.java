package pk.lucidxpo.ynami.testutils;

import java.security.SecureRandom;
import java.util.UUID;

import static java.lang.Integer.MAX_VALUE;
import static org.apache.commons.lang3.StringUtils.remove;

public final class Identity {

    private static final int MAX_DIGITS = 12;
    private static final int RANDOM_DIGITS_9 = 9;
    private static final int RANDOM_DIGITS_10 = 10;
    private static final int RANDOM_DIGITS_11 = 11;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static int randomInt() {
        return RANDOM.nextInt(MAX_VALUE);
    }

    public static int randomInt(final int minValue, final int maxValue) {
        return minValue + RANDOM.nextInt(maxValue - minValue);
    }

    public static String randomID(final int digits) {
        return randomID("", digits);
    }

    public static String randomID() {
        return remove(randomUUID(), "-");
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String randomID9(final String prefix) {
        return randomID(prefix, RANDOM_DIGITS_9);
    }

    public static String randomID11(final String prefix) {
        return randomID(prefix, RANDOM_DIGITS_11);
    }

    public static String randomID12(final String prefix) {
        return randomID(prefix, MAX_DIGITS);
    }

    public static String randomID(final String prefix, final int digits) {
        final int len = prefix.length() + digits;
        final StringBuilder buf = new StringBuilder(len);
        buf.append(prefix);
        for (int i = 0; i < digits; i++) {
            buf.append(RANDOM.nextInt(RANDOM_DIGITS_10));
        }
        return buf.toString();
    }
}
