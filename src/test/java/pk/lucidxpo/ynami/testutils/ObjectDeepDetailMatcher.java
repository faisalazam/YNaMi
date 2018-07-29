package pk.lucidxpo.ynami.testutils;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;

import static javax.xml.bind.JAXBContext.newInstance;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

public class ObjectDeepDetailMatcher extends BaseMatcher<Object> {

    private final Class aClass;
    private final Object expected;

    public static ObjectDeepDetailMatcher equivalentTo(final Object expected) {
        return new ObjectDeepDetailMatcher(expected);
    }

    public ObjectDeepDetailMatcher(final Object expected) {
        this.expected = expected;
        this.aClass = expected.getClass();
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


    private String toXML(final Object object) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final JAXBElement<?> rootElement = new JAXBElement<>(new QName(aClass.getSimpleName()), aClass, aClass.cast(object));
        final JAXBContext jaxbContext = newInstance(aClass);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(rootElement, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }

    @Override
    public void describeTo(final Description description) {
        description.appendValue(expected);
    }
}
