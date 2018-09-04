package it.pk.lucidxpo.ynami.spring.security;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.persistence.dao.security.RoleRepository;
import pk.lucidxpo.ynami.persistence.dao.security.UserRepository;
import pk.lucidxpo.ynami.persistence.model.security.Role;
import pk.lucidxpo.ynami.persistence.model.security.User;
import pk.lucidxpo.ynami.spring.security.UserPrincipal;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.util.List;
import java.util.Set;

import static it.pk.lucidxpo.ynami.spring.security.UserDetailsServiceIntegrationTest.ADMIN_USER;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static pk.lucidxpo.ynami.persistence.model.security.UserBuilder.anUser;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.WEB_SECURITY;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@WithUserDetails(value = ADMIN_USER)
@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class UserDetailsServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        featureManager.activate(WEB_SECURITY);
        assertTrue(featureManager.isActive(WEB_SECURITY));
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionOnLoadUserByUsernameWhenUserWithSpecifiedUsernameOrEmailDoesNotExist() {
        final String usernameOrEmail = randomAlphanumeric(5, 35);
        try {
            userDetailsService.loadUserByUsername(usernameOrEmail);
            fail("Should have thrown UsernameNotFoundException as there is no user with specified username or email: " + usernameOrEmail);
        } catch (final Exception e) {
            assertThat(e, instanceOf(UsernameNotFoundException.class));
        }
    }

    @Test
    void shouldGetUserDetailsOnLoadUserByUsernameWhenUserWithSpecifiedUsernameDoesExist() {
        final String username = randomAlphanumeric(5, 35);
        final User user = anUser().withUsername(username).build();

        saveAndAssertUserDetails(username, user);
    }

    @Test
    void shouldGetUserDetailsOnLoadUserByUsernameWhenUserWithSpecifiedEmailDoesExist() {
        final String email = randomAlphanumeric(5) + "@" + randomAlphanumeric(5) + "." + randomAlphanumeric(3);
        final User user = anUser().withEmail(email).build();

        saveAndAssertUserDetails(email, user);
    }

    private void saveAndAssertUserDetails(final String usernameOrEmail, final User user) {
        final Set<Role> roles = chooseOneOf(getRolesCollection(roleRepository));
        user.setRoles(roles);

        final User savedUser = userRepository.save(user);
        assertAuditInfo(savedUser, ADMIN_USER);

        final UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(usernameOrEmail);
        final List<? extends GrantedAuthority> expectedAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(toList());
        assertUserDetails(savedUser, userDetails, expectedAuthorities);
    }

    private void assertUserDetails(final User expectedUserDetails,
                                   final UserPrincipal actualUserDetails,
                                   final List<? extends GrantedAuthority> expectedAuthorities) {
        assertAll(
                () -> assertEquals(expectedUserDetails.getId(), actualUserDetails.getId()),
                () -> assertEquals(expectedUserDetails.getName(), actualUserDetails.getName()),
                () -> assertEquals(expectedUserDetails.getUsername(), actualUserDetails.getUsername()),
                () -> assertEquals(expectedUserDetails.getEmail(), actualUserDetails.getEmail()),
                () -> assertEquals(expectedUserDetails.getPassword(), actualUserDetails.getPassword()),
                () -> assertTrue(actualUserDetails.isAccountNonExpired()),
                () -> assertTrue(actualUserDetails.isAccountNonLocked()),
                () -> assertTrue(actualUserDetails.isCredentialsNonExpired()),
                () -> assertTrue(actualUserDetails.isEnabled()),
                () -> assertTrue(actualUserDetails.getAuthorities().containsAll(expectedAuthorities))
        );
    }
}