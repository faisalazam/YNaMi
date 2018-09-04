package it.pk.lucidxpo.ynami.spring.security.helper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pk.lucidxpo.ynami.persistence.model.Identifiable;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.valueOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.StringUtils.uncapitalize;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

public class RequestMappingCustomizer {
    private final Class entityClazz;
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
                                    final Class entityClazz,
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

    void assertExpectations(final ResultActions resultActions) throws Exception {
        if (isNotEmpty(resultMatchers)) {
            for (final ResultMatcher resultMatcher : resultMatchers) {
                resultActions.andExpect(resultMatcher);
            }
        } else {
            resultActions.andExpect(status().isOk());
        }
    }

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

    static Object[] uriVariables(final String urlTemplate) {
        final Pattern pathVariablesCapturingPattern = compile("\\{(.*?)}");
        final Matcher matcher = pathVariablesCapturingPattern.matcher(urlTemplate);
        final List<MatchResult> pathVariables = matcher.results().collect(toList());
        return range(0, pathVariables.size()).mapToObj(i -> randomID()).toArray();
    }

    @SuppressWarnings("unchecked")
    private Identifiable saveEntity(final ApplicationContext applicationContext) {
        final Object entity = new PodamFactoryImpl().manufacturePojo(entityClazz);
        assertNotNull(entity);

        final String entitySimpleName = requireNonNull(entityClazz).getSimpleName();
        final JpaRepository repository = (JpaRepository) applicationContext.getBean(uncapitalize(entitySimpleName) + "Repository");
        return (Identifiable) repository.saveAndFlush(entity);
    }
}
