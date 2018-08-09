package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${config.web.security.enabled:true}")
    private boolean securityEnabled;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (!securityEnabled) {
            http
                    .csrf().disable()
                    .authorizeRequests().anyRequest().permitAll();
            return;
        }
        http
                .authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    @ConditionalOnProperty(name = "config.web.security.enabled", havingValue = "true", matchIfMissing = true)
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        if (securityEnabled) {
            auth.inMemoryAuthentication().passwordEncoder(passwordEncoder());
        }
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        if (securityEnabled) {
            auth.inMemoryAuthentication().withUser("john123").password(passwordEncoder().encode("password")).roles("USER");
            auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
        }
    }
}