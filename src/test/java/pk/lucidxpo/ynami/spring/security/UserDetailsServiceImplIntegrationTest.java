package pk.lucidxpo.ynami.spring.security;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.dao.security.RoleRepository;
import pk.lucidxpo.ynami.persistence.dao.security.UserRepository;
import pk.lucidxpo.ynami.persistence.model.security.Role;
import pk.lucidxpo.ynami.persistence.model.security.User;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static pk.lucidxpo.ynami.persistence.model.security.User.builder;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@TestPropertySource(properties = {
        "config.web.security.enabled=true"
})
public class UserDetailsServiceImplIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void shouldThrowUsernameNotFoundExceptionOnLoadUserByUsernameWhenUserWithSpecifiedUsernameOrEmailDoesNotExist() {
        final String usernameOrEmail = randomAlphanumeric(5, 35);
        try {
            userDetailsService.loadUserByUsername(usernameOrEmail);
            fail("Should have thrown UsernameNotFoundException as there is no user with specified username or email: " + usernameOrEmail);
        } catch (Exception e) {
            assertThat(e, instanceOf(UsernameNotFoundException.class));
        }
    }

    @Test
    public void shouldGetUserDetailsOnLoadUserByUsernameWhenUserWithSpecifiedUsernameDoesExist() {
        final String username = randomAlphanumeric(5, 35);
        final User user = builder()
                .name(randomAlphanumeric(5, 35))
                .username(username)
                .email(randomAlphanumeric(5) + "@" + randomAlphanumeric(5) + "." + randomAlphanumeric(3))
                .password(randomAlphanumeric(5, 35))
                .build();

        saveAndAssertUserDetails(username, user);
    }

    @Test
    public void shouldGetUserDetailsOnLoadUserByUsernameWhenUserWithSpecifiedEmailDoesExist() {
        final String email = randomAlphanumeric(5) + "@" + randomAlphanumeric(5) + "." + randomAlphanumeric(3);
        final User user = builder()
                .name(randomAlphanumeric(5, 35))
                .username(randomAlphanumeric(5, 35))
                .email(email)
                .password(randomAlphanumeric(5, 35))
                .build();

        saveAndAssertUserDetails(email, user);
    }

    private void saveAndAssertUserDetails(final String usernameOrEmail, final User user) {
        final Set<Role> roles = chooseOneOf(getRolesCollection(roleRepository));
        user.setRoles(roles);

        final User savedUser = userRepository.save(user);
        assertThat(savedUser.getCreatedBy(), is("Crazy"));
        assertThat(savedUser.getLastModifiedBy(), is("Crazy"));
        assertThat(savedUser.getCreatedDate(), notNullValue());
        assertThat(savedUser.getLastModifiedDate(), notNullValue());

        final UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(usernameOrEmail);
        final List<? extends GrantedAuthority> expectedAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(toList());
        assertUserDetails(savedUser, userDetails, expectedAuthorities);
    }

    private void assertUserDetails(final User expectedUserDetails,
                                   final UserPrincipal actualUserDetails,
                                   final List<? extends GrantedAuthority> expectedAuthorities) {
        assertThat(actualUserDetails.getId(), is(expectedUserDetails.getId()));
        assertThat(actualUserDetails.getName(), is(expectedUserDetails.getName()));
        assertThat(actualUserDetails.getUsername(), is(expectedUserDetails.getUsername()));
        assertThat(actualUserDetails.getEmail(), is(expectedUserDetails.getEmail()));
        assertThat(actualUserDetails.getPassword(), is(expectedUserDetails.getPassword()));
        assertThat(actualUserDetails.isAccountNonExpired(), is(true));
        assertThat(actualUserDetails.isAccountNonLocked(), is(true));
        assertThat(actualUserDetails.isCredentialsNonExpired(), is(true));
        assertThat(actualUserDetails.isEnabled(), is(true));
        assertThat(actualUserDetails.getAuthorities().containsAll(expectedAuthorities), is(true));
    }
}