package pk.lucidxpo.ynami.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pk.lucidxpo.ynami.spring.aspect.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@Configuration
@EnableWebSecurity
// TODO Spring Upgrade: Compare this file with the one before the upgrade and Fix me
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    static final String LOGOUT_URL = "/perform_logout";
    static final String LOGOUT_SUCCESS_URL = "/login?logout";
    static final String LOGIN_PROCESSING_URL = "/perform_login";

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!featureManager.isActive(WEB_SECURITY)) {
            http
//                    .csrf().disable()
                    .authorizeRequests().anyRequest().permitAll();
            return http.build();
        }
        http
                .authorizeRequests().anyRequest().authenticated()

//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl(LOGIN_PROCESSING_URL)
//                .permitAll()

//                .and()
//                .logout()
//                .logoutUrl(LOGOUT_URL)
//                .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
        ;
        return http.build();

//        http.csrf()
//                .disable()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.DELETE)
//                .hasRole("ADMIN")
//                .antMatchers("/admin/**")
//                .hasAnyRole("ADMIN")
//                .antMatchers("/user/**")
//                .hasAnyRole("USER", "ADMIN")
//                .antMatchers("/login/**")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .httpBasic()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        return http.build();/
    }

    @Bean
    @FeatureAssociation(value = WEB_SECURITY)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(securityDebug).ignoring().anyRequest();
        // TODO Spring upgrade: fix me properly
//        .requestMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico");
    }
}