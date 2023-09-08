package pk.lucidxpo.ynami.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.springframework.http.CacheControl.maxAge;
import static pk.lucidxpo.ynami.spring.security.SecurityConfig.LOGIN_PAGE_URL;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/webjars/**"
                ).addResourceLocations(
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath:/static/img/",
                        "classpath:/META-INF/resources/webjars/"
                ).setCacheControl(maxAge(30L, DAYS).cachePublic())
                .resourceChain(true);
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController(LOGIN_PAGE_URL).setViewName("login");
    }
}