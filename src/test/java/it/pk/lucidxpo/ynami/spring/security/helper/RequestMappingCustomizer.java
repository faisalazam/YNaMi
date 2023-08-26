package it.pk.lucidxpo.ynami.spring.security.helper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pk.lucidxpo.ynami.persistence.model.Identifiable;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.compile;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.valueOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.StringUtils.uncapitalize;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

/*
 * RequestMappingCustomizer: This class is responsible for customizing the request mappings.
 *
 * It'll look at the request url template and figure out if there are any path variables, and if so, then it'll fill
 * them with random values.
 *
 * If it involves any entity, then it'll create the Pojo for that entity filled with the random data and save it in the
 * database using that entity's corresponding JPA repository.'
 *
 * Then it'll fill the 'resultActions' object with the matchers from the 'resultMatchers' property which will be used
 * later on to perform assertions.
 */
public class RequestMappingCustomizer {
    private final Class<?> entityClazz;
    private final String methodType;
    private final String urlTemplate;
    private Pair<String, Object> modelAttribute;
    private final ResultMatcher[] resultMatchers;
    private final MockHttpServletRequestBuilder requestBuilder;

    public RequestMappingCustomizer(final String methodType,
                                    final String urlTemplate,
                                    final ResultMatcher... resultMatchers) {
        this.entityClazz = null;
        this.requestBuilder = null;
        this.methodType = methodType;
        this.urlTemplate = urlTemplate;
        this.resultMatchers = resultMatchers;
    }

    public RequestMappingCustomizer(final String methodType,
                                    final String urlTemplate,
                                    final Class<?> entityClazz,
                                    final ResultMatcher... resultMatchers) {
        this.requestBuilder = null;
        this.methodType = methodType;
        this.entityClazz = entityClazz;
        this.urlTemplate = urlTemplate;
        this.resultMatchers = resultMatchers;
    }

    public RequestMappingCustomizer(final String methodType,
                                    final MockHttpServletRequestBuilder requestBuilder,
                                    final ResultMatcher... resultMatchers) {
        this.entityClazz = null;
        this.urlTemplate = EMPTY;
        this.methodType = methodType;
        this.requestBuilder = requestBuilder;
        this.resultMatchers = resultMatchers;
    }

    /**
     * This method will fill the 'resultActions' object with the matchers from the 'resultMatchers' property.
     *
     * @param resultActions: the result actions object to contain the expectations or assertions, which will be executed
     *                       later on.
     */
    void assertExpectations(final ResultActions resultActions) throws Exception {
        if (isNotEmpty(resultMatchers)) {
            for (final ResultMatcher resultMatcher : resultMatchers) {
                resultActions.andExpect(resultMatcher);
            }
        } else {
            resultActions.andExpect(status().isOk());
        }
    }

    /**
     * This method is used to return configured MockHttpServletRequestBuilder object which will be used to build the request.
     *
     * @return MockHttpServletRequestBuilder
     */
    MockHttpServletRequestBuilder getRequestBuilder(final ApplicationContext applicationContext) {
        final MockHttpServletRequestBuilder requestBuilder;
        if (this.requestBuilder != null) {
            requestBuilder = this.requestBuilder;
        } else if (entityClazz != null) {
            final Identifiable identifiable = saveEntity(applicationContext);
            requestBuilder = request(valueOf(methodType), urlTemplate, identifiable.getId());
        } else {
            requestBuilder = request(valueOf(methodType), urlTemplate, uriVariables(urlTemplate));
        }

        if (modelAttribute != null) {
            requestBuilder.flashAttr(modelAttribute.getKey(), modelAttribute.getValue());
        }
        return requestBuilder;
    }

    String getMethodType() {
        return methodType;
    }

    void setModelAttribute(Pair<String, Object> modelAttribute) {
        this.modelAttribute = modelAttribute;
    }

    /*
     * This method is called to build an array of objects containing the values for the path variables present in the
     * @param urlTemplate. The @param urlTemplate may or may not contain path variables.
     */
    static Object[] uriVariables(final String urlTemplate) {
        final Pattern pathVariablesCapturingPattern = compile("\\{(.*?)}");
        final Matcher matcher = pathVariablesCapturingPattern.matcher(urlTemplate);
        final int numberOfPathVariables = (int) matcher.results().count();
        return range(0, numberOfPathVariables).mapToObj(i -> randomID()).toArray();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    /*
     * This method is used to create a new instance of 'entityClazz' filled with random data using Podam.
     * Then it does retrieve the corresponding JPA repository from the application context for the specified entityClazz.
     * And finally uses the retrieved repository to save the newly created instance in db.
     *
     * entityClazz: Class, of which Pojo will be created and filled with random data.
     */
    private Identifiable saveEntity(final ApplicationContext applicationContext) {
        // PODAM is a lightweight tool to autofill Java POJOs with data. So, the following statement will create an
        // instance of 'entityClazz' filled with random data.
        final Object entity = new PodamFactoryImpl().manufacturePojo(entityClazz);
        assertNotNull(entity);

        final String entitySimpleName = requireNonNull(entityClazz).getSimpleName();
        final String entityRepositoryName = uncapitalize(entitySimpleName) + "Repository";
        final JpaRepository repository = (JpaRepository) applicationContext.getBean(entityRepositoryName);
        return (Identifiable) repository.saveAndFlush(entity);
    }
}
