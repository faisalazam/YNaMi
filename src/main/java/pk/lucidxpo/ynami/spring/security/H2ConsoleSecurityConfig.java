package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Profile("h2")
@Configuration
@EnableWebSecurity
// TODO Spring Upgrade: compare it with SecurityConfig class and see if it has to be toggled and add tests
public class H2ConsoleSecurityConfig {
    final String h2ConsolePattern;

    public H2ConsoleSecurityConfig(@Value("${spring.h2.console.path:h2-console}") String h2ConsolePath) {
        this.h2ConsolePattern = h2ConsolePath + "/**";
    }

    @Bean
    public SecurityFilterChain h2ConsoleSecurityFilterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.requestMatchers(antMatcher(h2ConsolePattern)).permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(antMatcher(h2ConsolePattern)))
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
        ;
        return http.build();
    }
}