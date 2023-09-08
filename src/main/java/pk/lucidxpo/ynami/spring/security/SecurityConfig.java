package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import pk.lucidxpo.ynami.spring.aspect.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;
import pk.lucidxpo.ynami.utils.ProfileManager;

import static org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final String LOGIN_PAGE_URL = "/login.html";
    static final String LOGIN_PROCESSING_URL = "/perform_login";
    public static final String LOGIN_FAILURE_URL = LOGIN_PAGE_URL + "?error=true";

    static final String LOGOUT_URL = "/perform_logout";
    static final String LOGOUT_SUCCESS_URL = LOGIN_PAGE_URL + "?logout";

    private static final AntPathRequestMatcher[] ENDPOINTS_WHITELIST = {
            antMatcher("/css/**"),
            antMatcher("/js/**"),
            antMatcher("/img/**"),
            antMatcher("/webjars/**"),
            antMatcher("/favicon.ico") // 403 not working
    };

    /**
     * Setting content security policy header to fix "Content Security Policy (CSP) Header Not Set" reported by ZAP.
     * <p>
     * The header value can be picked from the
     * <a href="https://owasp.org/www-project-secure-headers/ci/headers_add.json"> HTTP response security headers to add</a>
     */
    private static final String CONTENT_POLICY_DIRECTIVES = "default-src 'self'"
            + "; form-action 'self'"
            + "; object-src 'none'"
            + "; frame-ancestors 'none'"
            + "; upgrade-insecure-requests"
            + "; block-all-mixed-content"
            + "; report-uri /report"
            + "; report-to csp-violation-report";

    private final String h2ConsolePattern;
    private final ProfileManager profileManager;
    private final FeatureManagerWrappable featureManager;

    @Autowired
    public SecurityConfig(@Value("${spring.h2.console.path:/h2-console}") final String h2ConsolePath,
                          final ProfileManager profileManager,
                          final FeatureManagerWrappable featureManager) {
        this.profileManager = profileManager;
        this.featureManager = featureManager;
        this.h2ConsolePattern = h2ConsolePath.endsWith("/") ? h2ConsolePath + "**" : h2ConsolePath + "/**";
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // TODO: do I need @FeatureAssociation(value = WEB_SECURITY) it here???
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
//        TODO: might need this for a security fix mentioned by the ZAP/security tests but bdd/selenium tests fail with
//        this, need a proper solution.
//        http
//                .headers(headers -> headers
//                        .contentSecurityPolicy(contentSecurityPolicy -> // TODO: add tests for contentSecurityPolicy header
//                                contentSecurityPolicy.policyDirectives(CONTENT_POLICY_DIRECTIVES)
//                        )
//                );

        // TODO: does this need to be inside any other condition?? Cover it through integration tests.
        if (profileManager.isH2Active()) {
            setupH2ConsoleSecurity(http);
        }

        if (!featureManager.isActive(WEB_SECURITY)) {
            return configureInsecureAccess(http);
        }

        return http
                .authorizeHttpRequests((auth) -> auth.requestMatchers(ENDPOINTS_WHITELIST).permitAll())

                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())

                // One reason to override most of the defaults in Spring Security is to hide that the application
                // is secured with Spring Security. We also want to minimize the information a potential attacker
                // knows about the application.
                .formLogin(this::configureFormLogin)

                .logout(this::configureFormLogout)

                .build();
    }

    @Bean
    @FeatureAssociation(value = WEB_SECURITY)
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // TODO: do I need @FeatureAssociation(value = WEB_SECURITY) it here???
    public void setupH2ConsoleSecurity(final HttpSecurity http) throws Exception {
        final RequestMatcher h2PathMatcher = new AntPathRequestMatcher(h2ConsolePattern);
        final XFrameOptionsHeaderWriter sameOriginXFrameHeaderWriter = new XFrameOptionsHeaderWriter(SAMEORIGIN);
        // DelegatingRequestMatcherHeaderWriter is used to apply the XFrame header only to the h2 path. On the other
        // hand, if we had used .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)),
        // then it'd have applied the XFrame header to all the paths/urls.
        final DelegatingRequestMatcherHeaderWriter headerWriter =
                new DelegatingRequestMatcherHeaderWriter(h2PathMatcher, sameOriginXFrameHeaderWriter);
        http
                .authorizeHttpRequests(auth -> auth.requestMatchers(h2PathMatcher).permitAll())
                .headers(headers -> headers.addHeaderWriter(headerWriter))
        ;
    }

    private DefaultSecurityFilterChain configureInsecureAccess(final HttpSecurity http) throws Exception {
        return http
                // Spring's recommendation is to use CSRF protection for any request that could be processed by a
                // browser by normal users. If you are only creating a service that is used by non-browser clients,
                // you will likely want to disable CSRF protection. If our stateless API uses token-based
                // authentication, such as JWT, we don't need CSRF protection, and we must disable.
                // However, if our stateless API uses a session cookie authentication, we need to enable CSRF protection
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
                .build();
    }

    private void configureFormLogin(final FormLoginConfigurer<HttpSecurity> formLoginConfigurer) {
        formLoginConfigurer
                .loginPage(LOGIN_PAGE_URL)
                // By overriding this default URL, we’re concealing that the application is actually secured
                // with Spring Security. This information should not be available externally.
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .permitAll()
                .failureUrl(LOGIN_FAILURE_URL);
    }

    private void configureFormLogout(LogoutConfigurer<HttpSecurity> logoutConfigurer) {
        logoutConfigurer
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

//    @Bean
//    @FeatureAssociation(value = WEB_SECURITY)
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(
//                antMatcher("/css/**"),
//                antMatcher("/js/**"),
//                antMatcher("/img/**"),
//                antMatcher("/webjars/**")
//        );
//    }
}