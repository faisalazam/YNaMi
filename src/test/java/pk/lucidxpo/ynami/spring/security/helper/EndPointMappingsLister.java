package pk.lucidxpo.ynami.spring.security.helper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.valueOf;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static pk.lucidxpo.ynami.spring.security.helper.RequestMappingCustomizer.uriVariables;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.createRandomlyFilledObject;

public class EndPointMappingsLister {
    private final List<String> excludedEndpointMappings;
    private final Map<String, RequestMappingCustomizer> customizedEndpointMappingsMap;

    public EndPointMappingsLister(final List<String> excludedEndpointMappings,
                                  final Map<String, RequestMappingCustomizer> customizedEndpointMappingsMap) {
        this.excludedEndpointMappings = excludedEndpointMappings;
        this.customizedEndpointMappingsMap = customizedEndpointMappingsMap;
    }

    @SuppressWarnings("unchecked")
    public List<Pair<String, RequestMappingCustomizer>> endPointMappingsCollection(final AbstractHandlerMethodMapping appEndpointHandlerMapping) {
        final Map<RequestMappingInfo, HandlerMethod> endpointHandlerMethods = appEndpointHandlerMapping.getHandlerMethods();
        assertFalse(endpointHandlerMethods.isEmpty());

        final List<Pair<String, RequestMappingCustomizer>> endpointMappingPairs = endpointHandlerMethods.entrySet().stream()
                .flatMap(this::endpointMappingsStream)
                .filter(endpointMappingPair -> !excludedEndpointMappings.contains(endpointMappingPair.getKey()))
                .collect(toList());
        assertFalse(endpointMappingPairs.isEmpty());
        return endpointMappingPairs;
    }

    private Stream<Pair<String, RequestMappingCustomizer>> endpointMappingsStream(final Entry<RequestMappingInfo, HandlerMethod> handlerMethod) {
        final RequestMappingInfo requestMappingInfo = handlerMethod.getKey();
        final Set<RequestMethod> requestMethods = requestMappingInfo.getMethodsCondition().getMethods();

        final Method method = (Method) getField(handlerMethod.getValue(), "method");
        assertNotNull(method);

        final Pair<String, Object> modelAttribute = getModelAttribute(method);

        final Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
        assertFalse(patterns.isEmpty());

        return patterns.stream()
                .flatMap(pattern -> endpointMappingsStream(pattern, requestMethods, modelAttribute));
    }

    private Stream<Pair<String, RequestMappingCustomizer>> endpointMappingsStream(final String pattern,
                                                                                  final Set<RequestMethod> requestMethods,
                                                                                  final Pair<String, Object> modelAttribute) {
        return requestMethods.isEmpty()
                ? of(endpointMappingPair("GET", pattern, modelAttribute))
                : requestMethods.stream().map(methodType -> endpointMappingPair(methodType.name(), pattern, modelAttribute));
    }

    private Pair<String, RequestMappingCustomizer> endpointMappingPair(final String methodType,
                                                                       final String urlTemplate,
                                                                       final Pair<String, Object> modelAttribute) {
        switch (methodType) {
            case "GET":
            case "POST":
            case "PUT":
            case "PATCH":
            case "DELETE":
                final RequestMappingCustomizer requestMappingCustomizer;
                final String requestMapping = format("[%s] %s", methodType, urlTemplate);
                if (customizedEndpointMappingsMap.containsKey(requestMapping)) {
                    requestMappingCustomizer = customizedEndpointMappingsMap.get(requestMapping);
                } else {
                    requestMappingCustomizer = new RequestMappingCustomizer(methodType, request(valueOf(methodType), urlTemplate, uriVariables(urlTemplate)));
                }
                requestMappingCustomizer.setModelAttribute(modelAttribute);
                return of(requestMapping, requestMappingCustomizer);
            default:
                throw new UnsupportedOperationException(methodType + " is not supported. Add a case to support it.");
        }
    }

    private Pair<String, Object> getModelAttribute(final Method handlerMethod) {
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