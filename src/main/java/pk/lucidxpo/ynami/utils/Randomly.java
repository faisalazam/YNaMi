package pk.lucidxpo.ynami.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.joda.time.DateTimeZone.UTC;

public final class Randomly {
    public static final Random RANDOM = new Random(new Date().getTime());

    private Randomly() {
    }

    @SafeVarargs
    public static <T> T chooseOneOf(final T... values) {
        return chooseOneOf(asList(values));
    }

    public static <T> T chooseOneOf(final Iterable<T> values) {
        return chooseOneOf(list(values));
    }

    private static <T> T chooseOneOf(final List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List was empty");
        }
        return list.get((int) chooseNumberBetween(0L, list.size() - 1L));
    }

    private static <T> List<T> list(final Iterable<T> values) {
        return newArrayList(values);
    }

    public static long chooseNumberBetween(final long from, final long to) {
        final long range = to - from + 1;
        final long fraction = (long) (range * RANDOM.nextDouble());
        return from + fraction;
    }

    public static LocalDateTime chooseLocalDateBetween(final LocalDateTime from, final LocalDateTime to) {
        return chooseDateBetween(
                from.toDateTime(UTC),
                to.toDateTime(UTC)).
                toLocalDateTime();
    }

    public static DateTime chooseDateBetween(final DateTime from, final DateTime to) {
        final long difference = to.getMillis() - from.getMillis();
        return new DateTime(from.getMillis() + chooseNumberBetween(0, difference));
    }
}
