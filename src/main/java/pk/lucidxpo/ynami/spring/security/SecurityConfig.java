package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private FeatureManagerWrappable featureManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (!featureManager.isActive(WEB_SECURITY)) {
            http
                    .csrf().disable()
                    .authorizeRequests().anyRequest().permitAll();
            return;
        }
        http
                .authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        if (featureManager.isActive(WEB_SECURITY)) {
            auth.inMemoryAuthentication().passwordEncoder(passwordEncoder());
        }
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        if (featureManager.isActive(WEB_SECURITY)) {
            auth.inMemoryAuthentication().withUser("john123").password(passwordEncoder().encode("password")).roles("USER");
            auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
        }
    }
}