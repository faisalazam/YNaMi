package pk.lucidxpo.ynami;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected Environment environment;

    @Autowired
    protected ApplicationContext applicationContext;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Test
    public void contextLoads() {
    }

    protected boolean acceptsProfile(final String... profiles) {
        return environment.acceptsProfiles(profiles);
    }

    protected boolean isConfigEnabled(final String config) {
        return toBoolean(environment.getProperty(config));
    }

    protected void assertBeanDoesNotExist(final Class<?> beanClass) {
        try {
            applicationContext.getBean(beanClass);
            fail("Should have thrown 'NoSuchBeanDefinitionException' as there shouldn't exist any such bean");
        } catch (Exception e) {
            assertThat(e, instanceOf(NoSuchBeanDefinitionException.class));
        }
    }
}