package pk.lucidxpo.ynami;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static org.springframework.boot.SpringApplication.run;
import static pk.lucidxpo.ynami.spring.features.TogglzWrapper.bindApplicationContext;
import static pk.lucidxpo.ynami.spring.features.TogglzWrapper.getApplicationContext;
import static pk.lucidxpo.ynami.spring.features.TogglzWrapper.releaseApplicationContext;

@SpringBootApplication
public class YNaMiApplication implements ApplicationContextAware {

    public static void main(String[] args) {
        run(YNaMiApplication.class, args);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        if (getApplicationContext() != null) {
            releaseApplicationContext();
        }
        bindApplicationContext(applicationContext);
    }
}