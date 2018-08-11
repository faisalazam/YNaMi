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
import pk.lucidxpo.ynami.persistence.dao.security.RoleRepository;
import pk.lucidxpo.ynami.persistence.model.security.Role;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static pk.lucidxpo.ynami.persistence.model.security.RoleName.values;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractIntegrationTest {
    public static final String ADMIN_USER = "admin";

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

    /*
     * This method will return a collection of 'Set<Role>', where each 'Set<Role>' will have different size.
     */
    protected Collection<Set<Role>> getRolesCollection(final RoleRepository roleRepository) {
        final List<Role> allRoles = stream(values())
                .map(roleName -> roleRepository.findByName(roleName).get())
                .collect(toList());
        assertThat(allRoles.size(), is(values().length));

        final List<Set<Role>> associatedRolesList = newArrayList();
        for (int i = 1; i <= allRoles.size(); i++) {
            associatedRolesList.add(newHashSet(allRoles.subList(0, i)));
        }
        assertThat(associatedRolesList.size(), is(values().length));

        return associatedRolesList;
    }
}