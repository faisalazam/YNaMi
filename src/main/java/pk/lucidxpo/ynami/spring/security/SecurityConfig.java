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
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pk.lucidxpo.ynami.spring.aspect.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;
import pk.lucidxpo.ynami.utils.ProfileManager;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    static final String LOGOUT_URL = "/perform_logout";
    static final String LOGOUT_SUCCESS_URL = "/login?logout";
    static final String LOGIN_PROCESSING_URL = "/perform_login";

    private final String h2ConsolePattern;
    private final ProfileManager profileManager;
    private final FeatureManagerWrappable featureManager;

    @Autowired
    public SecurityConfig(@Value("${spring.h2.console.path:h2-console}") final String h2ConsolePath,
                          final ProfileManager profileManager,
                          final FeatureManagerWrappable featureManager) {
        this.profileManager = profileManager;
        this.featureManager = featureManager;
        this.h2ConsolePattern = h2ConsolePath + "/**";
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // TODO: do I need @FeatureAssociation(value = WEB_SECURITY) it here???
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        // TODO: does this need to be inside any other condition?? Cover it through integration tests.
        if (profileManager.isH2Active()) {
            setupH2ConsoleSecurity(http);
        }

        if (!featureManager.isActive(WEB_SECURITY)) {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
                    .build();
        }

        return http
                .authorizeHttpRequests((auth) -> auth.requestMatchers(
                                antMatcher("/css/**"),
                                antMatcher("/js/**"),
                                antMatcher("/img/**"),
                                antMatcher("/webjars/**"),
                                antMatcher("/favicon.ico") // 403 not working.
                        ).permitAll()
                )

                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())

                .formLogin(formLoginConfigurer -> formLoginConfigurer
                        .loginPage("/login")
                        .loginProcessingUrl(LOGIN_PROCESSING_URL)
                        .permitAll()
                )

                .logout(formLogoutConfigurer -> formLogoutConfigurer
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                )

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
        http
                .authorizeHttpRequests(auth -> auth.requestMatchers(antMatcher(h2ConsolePattern)).permitAll())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        ;
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