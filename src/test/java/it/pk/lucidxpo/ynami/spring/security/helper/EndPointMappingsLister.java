package it.pk.lucidxpo.ynami.spring.security.helper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import static it.pk.lucidxpo.ynami.spring.security.helper.RequestMappingCustomizer.uriVariables;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.valueOf;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.createRandomlyFilledObject;

/**
 * This class is responsible for getting all the endpoints along with their handler method in the application,
 * and then do some processing to convert them into a list of pairs of all the application's endpoints along with
 * their corresponding {@link RequestMappingCustomizer}
 */
public class EndPointMappingsLister {
    /**
     * The list of endpoint mappings to exclude from processing.
     */
    private final List<String> excludedEndpointMappings;

    /**
     * A map, where key is the request mapping -> url/endpoint with HTTP method type
     * (i.e. something like "[GET] /actuator/togglz/{name}", and the value is the customized endpoint mapping
     */
    private final Map<String, RequestMappingCustomizer> customizedEndpointMappingsMap;

    public EndPointMappingsLister(final List<String> excludedEndpointMappings,
                                  final Map<String, RequestMappingCustomizer> customizedEndpointMappingsMap) {
        this.excludedEndpointMappings = excludedEndpointMappings;
        this.customizedEndpointMappingsMap = customizedEndpointMappingsMap;
    }

    /**
     * This method is used to get all the endpoints along with their handler method in the application, and then do
     * some processing to convert them into a list of pairs of all the application's endpoints along with their
     * corresponding {@link RequestMappingCustomizer}
     *
     * @return a collection of pairs of all the application's endpoints along with their
     * corresponding {@link RequestMappingCustomizer}
     */
    public Collection<Pair<String, RequestMappingCustomizer>> endPointMappingsCollection(
            final AbstractHandlerMethodMapping<RequestMappingInfo> appEndpointHandlerMapping) {
        // A (read-only) map of all the application's endpoint mappings with their HandlerMethod.
        final Map<RequestMappingInfo, HandlerMethod> endpointToHandlerMethodMap = appEndpointHandlerMapping.getHandlerMethods();
        assertFalse(endpointToHandlerMethodMap.isEmpty());

        final Collection<Pair<String, RequestMappingCustomizer>> endpointMappingPairs = endpointToHandlerMethodMap.entrySet()
                .stream()
                .flatMap(this::endpointMappingsStream)
                .filter(endpointMappingPair -> !excludedEndpointMappings.contains(endpointMappingPair.getKey()))
                .collect(toList());
        assertFalse(endpointMappingPairs.isEmpty());
        return endpointMappingPairs;
    }

    /**
     * @param handlerMethod handled method for the http request
     * @return a stream of {@link EndPointMappingsLister#endpointMappingsStream}
     */
    private Stream<Pair<String, RequestMappingCustomizer>> endpointMappingsStream(
            final Entry<RequestMappingInfo, HandlerMethod> handlerMethod) {
        final RequestMappingInfo requestMappingInfo = handlerMethod.getKey();
        final Set<RequestMethod> requestMethods = requestMappingInfo.getMethodsCondition().getMethods();

        final Method method = (Method) getField(handlerMethod.getValue(), "method");
        assertNotNull(method);

        final Pair<String, Object> modelAttribute = getRandomlyFilledModelAttribute(method);

        final PathPatternsRequestCondition patternsCondition = requestMappingInfo.getPathPatternsCondition();
        assertNotNull(patternsCondition, "patternsCondition should not be null");

        final Set<String> patterns = patternsCondition.getPatternValues();
        assertFalse(patterns.isEmpty());

        return patterns.stream()
                .flatMap(pattern -> endpointMappingsStream(pattern, requestMethods, modelAttribute));
    }

    /**
     * @return a stream of {@link EndPointMappingsLister#endpointMappingPair}
     */
    private Stream<Pair<String, RequestMappingCustomizer>> endpointMappingsStream(final String pattern,
                                                                                  final Set<RequestMethod> requestMethods,
                                                                                  final Pair<String, Object> modelAttribute) {
        return requestMethods.isEmpty()
                ? Stream.of(endpointMappingPair("GET", pattern, modelAttribute))
                : requestMethods.stream().map(methodType -> endpointMappingPair(methodType.name(), pattern, modelAttribute));
    }

    /**
     * This method is used to build a pair of http request and customized endpoint mapping. See {@link RequestMappingCustomizer}
     *
     * @param methodType     HTTP method type -> "GET", "POST", "PUT", "PATCH", "DELETE"
     * @param urlTemplate    e.g. /actuator/togglz/{name}
     * @return A pair, where key is request mapping -> url/endpoint with HTTP method type
     * (i.e. something like "[GET] /actuator/togglz/{name}")
     * and value is the customized endpoint mapping. See {@link RequestMappingCustomizer}
     */
    private Pair<String, RequestMappingCustomizer> endpointMappingPair(final String methodType,
                                                                       final String urlTemplate,
                                                                       final Pair<String, Object> modelAttribute) {
        switch (methodType) {
            case "GET", "POST", "PUT", "PATCH", "DELETE" -> {
                final RequestMappingCustomizer requestMappingCustomizer;
                final String requestMapping = format("[%s] %s", methodType, urlTemplate);
                if (customizedEndpointMappingsMap.containsKey(requestMapping)) {
                    requestMappingCustomizer = customizedEndpointMappingsMap.get(requestMapping);
                } else {
                    requestMappingCustomizer = new RequestMappingCustomizer(methodType,
                            request(valueOf(methodType), urlTemplate, uriVariables(urlTemplate))
                    );
                }
                requestMappingCustomizer.setModelAttribute(modelAttribute);
                return of(requestMapping, requestMappingCustomizer);
            }
            default ->
                    throw new UnsupportedOperationException(methodType + " is not supported. Add a case to support it.");
        }
    }

    /**
     * This method will look for the method parameter which is annotated with {@link ModelAttribute}.
     * If it finds it, it will create a new instance of that type filled with the random data. Otherwise, null is returned.
     *
     * @param handlerMethod the method which will be handling the http request.
     * @return a pair of model attribute name and the corresponding randomly filled object of that class.
     */
    private Pair<String, Object> getRandomlyFilledModelAttribute(final Method handlerMethod) {
        final Parameter[] parameters = handlerMethod.getParameters();
        for (final Parameter parameter : parameters) {
            final ModelAttribute modelAttribute = parameter.getAnnotation(ModelAttribute.class);
            if (modelAttribute != null) {
                final Object randomlyFilledObject = createRandomlyFilledObject(parameter.getType());
                return of(modelAttribute.value(), randomlyFilledObject);
            }
        }
        return null;
    }
}