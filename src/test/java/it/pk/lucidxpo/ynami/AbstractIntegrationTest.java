package it.pk.lucidxpo.ynami;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pk.lucidxpo.ynami.YNaMiApplication;
import pk.lucidxpo.ynami.persistence.dao.security.RoleRepository;
import pk.lucidxpo.ynami.persistence.model.Auditable;
import pk.lucidxpo.ynami.persistence.model.security.Role;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static it.pk.lucidxpo.ynami.AbstractIntegrationTest.SCHEMA_NAME;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.joda.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.context.annotation.ComponentScan.Filter;
import static org.springframework.context.annotation.FilterType.REGEX;
import static org.springframework.core.env.Profiles.of;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static pk.lucidxpo.ynami.persistence.model.security.RoleName.values;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"spring.datasource.name=" + SCHEMA_NAME})
@ContextConfiguration(classes = {TestApplication.class, YNaMiApplication.class})
@ComponentScan(excludeFilters = @Filter(type = REGEX, pattern = "SeleniumTestCaseContext.class"))
public class AbstractIntegrationTest {
    @SuppressWarnings("WrongPropertyKeyValueDelimiter")
    public static final String SCHEMA_NAME = "IntegrationTestSchema";
    public static final String ADMIN_USER = "admin";
    protected static final String SUPPORT_USER = "support";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected Environment environment;

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected FeatureManagerWrappable featureManager;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @BeforeEach
    void before() {
        if (featureManager.isActive(WEB_SECURITY)) {
            mockMvc = webAppContextSetup(webApplicationContext)
                    .apply(springSecurity())
                    .build();
        }
    }

    @Test
    void contextLoads() {
    }

    boolean acceptsProfile(final String... profiles) {
        return environment.acceptsProfiles(of(profiles));
    }

    @SuppressWarnings("SameParameterValue")
    protected boolean isConfigEnabled(final String config) {
        return toBoolean(environment.getProperty(config));
    }

    protected void assertBeanDoesNotExist(final Class<?> beanClass) {
        try {
            applicationContext.getBean(beanClass);
            fail("Should have thrown 'NoSuchBeanDefinitionException' as there shouldn't exist any such bean");
        } catch (final Exception e) {
            assertThat(e, instanceOf(NoSuchBeanDefinitionException.class));
        }
    }

    /*
     * This method will return a collection of 'Set<Role>', where each 'Set<Role>' will have different size.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
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

    protected void assertAuditInfo(final Auditable<?> auditable, final String auditUser) {
        final String evaluatedAuditUser = featureManager.isActive(WEB_SECURITY) ? auditUser : "Anonymous";
        assertAll(
                () -> assertEquals(evaluatedAuditUser, auditable.getCreatedBy()),
                () -> assertEquals(evaluatedAuditUser, auditable.getLastModifiedBy()),
                () -> assertThat(auditable.getCreatedDate().toString(), containsString(now().toString())),
                () -> assertThat(auditable.getLastModifiedDate().toString(), containsString(now().toString()))
        );
    }
}