package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pk.lucidxpo.ynami.spring.aspect.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final FeatureManagerWrappable featureManager;

    @Autowired
    public SecurityConfig(final FeatureManagerWrappable featureManager) {
        this.featureManager = featureManager;
    }

    @Autowired
    @FeatureAssociation(value = WEB_SECURITY)
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("john123").password(passwordEncoder().encode("password")).roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

    @Override
    @FeatureAssociation(value = WEB_SECURITY)
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder());
    }

    @Override
    @FeatureAssociation(value = WEB_SECURITY)
    public void configure(final WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/webjars/**"
                );
    }
}