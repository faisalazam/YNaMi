package pk.lucidxpo.ynami.utils.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class EquivalenceMatcher<T> extends BaseMatcher<T> {
    private final T expected;
    private final String[] ignoredFields;
    private T actual;

    public EquivalenceMatcher(final T expected, final String... ignoredFields) {
        this.expected = expected;
        this.ignoredFields = ignoredFields;
    }

    public static <T> EquivalenceMatcher<T> equivalentTo(final T expected) {
        return new EquivalenceMatcher<>(expected);
    }

    public EquivalenceMatcher<T> ignoringFields(final String... fieldNames) {
        return new EquivalenceMatcher<>(expected, fieldNames);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(final Object actual) {
        if (expected == actual) {
            return true;
        }
        this.actual = (T) actual;
        return reflectionEquals(expected, this.actual, ignoredFields);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("\n\n actual : \n");
        description.appendText(reflectionToString(actual, MULTI_LINE_STYLE));
        description.appendText("expected to be equivalent to:\n");
        description.appendText(reflectionToString(expected, MULTI_LINE_STYLE));
    }
}
