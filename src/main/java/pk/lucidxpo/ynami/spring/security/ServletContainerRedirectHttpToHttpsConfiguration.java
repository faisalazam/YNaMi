package pk.lucidxpo.ynami.spring.security;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static java.lang.Integer.parseInt;
import static org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.DEFAULT_PROTOCOL;

@Configuration
@Profile("https")
@ConditionalOnProperty(name = "config.redirect.http.to.https.enabled", havingValue = "true")
public class ServletContainerRedirectHttpToHttpsConfiguration {
    @Bean
    public ServletWebServerFactory servletContainer(@Value("${server.secure.port}") final String serverSecurePort,
                                                    @Value("${server.insecure.port}") final String serverInsecurePort) {
        final TomcatServletWebServerFactory tomcatServletWebServerFactory = getTomcatServletWebServerFactory();
        final Connector portRedirectHttpConnector = getPortRedirectHttpConnector(serverSecurePort, serverInsecurePort);
        tomcatServletWebServerFactory.addAdditionalTomcatConnectors(portRedirectHttpConnector);
        return tomcatServletWebServerFactory;
    }

    private TomcatServletWebServerFactory getTomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(final Context context) {
                final SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");

                final SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);

                context.addConstraint(securityConstraint);
            }
        };
    }

    private Connector getPortRedirectHttpConnector(final String serverSecurePort, final String serverInsecurePort) {
        final Connector connector = new Connector(DEFAULT_PROTOCOL);
        connector.setSecure(false);
        connector.setScheme("http");
        connector.setPort(parseInt(serverInsecurePort));
        connector.setRedirectPort(parseInt(serverSecurePort));
        return connector;
    }
}