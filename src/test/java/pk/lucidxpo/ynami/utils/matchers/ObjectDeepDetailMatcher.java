package pk.lucidxpo.ynami.utils.matchers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.io.StringWriter;

public class ObjectDeepDetailMatcher extends BaseMatcher<Object> {

    private final Object expected;

    public static ObjectDeepDetailMatcher equivalentTo(final Object expected) {
        return new ObjectDeepDetailMatcher(expected);
    }

    private ObjectDeepDetailMatcher(final Object expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(final Object o) {

        try {
            final String actualOrderXML = toXML(o);
            final String expectedOrderXML = toXML(expected);
            return StringUtils.equals(actualOrderXML, expectedOrderXML);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private String toXML(final Object object) {
        final XStream xs = new XStream();
        final StringWriter writer = new StringWriter();
        xs.marshal(object, new CompactWriter(writer));
        return writer.toString();
    }

    @Override
    public void describeTo(final Description description) {
        description.appendValue(expected);
    }
}
