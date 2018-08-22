package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pk.lucidxpo.ynami.spring.aspect.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final FeatureManagerWrappable featureManager;

    @Autowired
    public SecurityConfig(final FeatureManagerWrappable featureManager,
                          @Qualifier("userDetailsServiceImpl") final UserDetailsService userDetailsService) {
        this.featureManager = featureManager;
        this.userDetailsService = userDetailsService;
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
                .authorizeRequests().anyRequest().authenticated()

                .and()
                .formLogin()

                .and()
                .logout()
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login?logout")
        ;
    }

    @Override
    @FeatureAssociation(value = WEB_SECURITY)
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
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