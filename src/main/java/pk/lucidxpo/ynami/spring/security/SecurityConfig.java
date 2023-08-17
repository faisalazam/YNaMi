package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pk.lucidxpo.ynami.spring.aspect.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    static final String LOGOUT_URL = "/perform_logout";
    static final String LOGOUT_SUCCESS_URL = "/login?logout";
    static final String LOGIN_PROCESSING_URL = "/perform_login";

    private final FeatureManagerWrappable featureManager;

    @Autowired
    public SecurityConfig(final FeatureManagerWrappable featureManager) {
        this.featureManager = featureManager;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // TODO: do I need @FeatureAssociation(value = WEB_SECURITY) it here???
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
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