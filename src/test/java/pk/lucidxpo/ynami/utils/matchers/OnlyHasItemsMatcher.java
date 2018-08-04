package pk.lucidxpo.ynami.utils.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class OnlyHasItemsMatcher<T extends Object> extends BaseMatcher<Collection<T>> {

    private final List<T> expectedItems;

    public OnlyHasItemsMatcher(final T... expectedItems) {
        this.expectedItems = asList(expectedItems);
    }

    public OnlyHasItemsMatcher(final List<T> expectedItems) {
        this.expectedItems = expectedItems;
    }

    @Override
    public boolean matches(final Object actual) {
        final Collection actualCollection = (Collection) actual;
        return actualCollection.size() == expectedItems.size() && actualCollection.containsAll(expectedItems);

    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Collection to only contains " + reflectionToString(expectedItems.toArray()));
    }
}
