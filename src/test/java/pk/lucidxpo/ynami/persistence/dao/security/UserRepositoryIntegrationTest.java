package pk.lucidxpo.ynami.persistence.dao.security;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.model.security.Role;
import pk.lucidxpo.ynami.persistence.model.security.User;
import pk.lucidxpo.ynami.utils.matchers.ObjectDeepDetailMatcher;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static pk.lucidxpo.ynami.persistence.model.security.User.UserBuilder;
import static pk.lucidxpo.ynami.persistence.model.security.User.builder;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@Transactional
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:insert-roles.sql")
public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldVerifyThatUserIsPersistedWithAuditInfoAndMultipleRoles() {
        for (Set<Role> associatedRoles : getRolesCollection(roleRepository)) {
            final String username = randomAlphanumeric(5, 35);
            final String email = randomAlphanumeric(5) + "@" + randomAlphanumeric(5) + "." + randomAlphanumeric(3);
            final User userWithSpecifiedUserName = builder()
                    .name(randomAlphanumeric(5, 35))
                    .username(username)
                    .email(email)
                    .password(randomAlphanumeric(5, 35))
                    .build();

            userWithSpecifiedUserName.setRoles(associatedRoles);

            final User savedUser = userRepository.save(userWithSpecifiedUserName);
            assertThat(savedUser.getName(), is(userWithSpecifiedUserName.getName()));
            assertThat(savedUser.getUsername(), is(userWithSpecifiedUserName.getUsername()));
            assertThat(savedUser.getEmail(), is(userWithSpecifiedUserName.getEmail()));
            assertThat(savedUser.getPassword(), is(userWithSpecifiedUserName.getPassword()));
            assertThat(savedUser.getCreatedBy(), is("Crazy"));
            assertThat(savedUser.getLastModifiedBy(), is("Crazy"));
            assertThat(savedUser.getCreatedDate(), notNullValue());
            assertThat(savedUser.getLastModifiedDate(), notNullValue());

            final Set<Role> savedUserRoles = savedUser.getRoles();
            assertThat(savedUserRoles.isEmpty(), is(false));
            assertThat(savedUserRoles.containsAll(associatedRoles), is(true));
        }
    }

    @Test
    public void shouldVerifyThatUserWithNonUniqueUsernameDoesNotGetPersisted() {
        final User savedUser = saveUser();

        try {
            saveUserWithUsername(savedUser.getUsername());
        } catch (Exception e) {
            assertThat(e, instanceOf(DataIntegrityViolationException.class));
        }
    }

    @Test
    public void shouldVerifyThatUserWithNonUniqueEmailDoesNotGetPersisted() {
        final User savedUser = saveUser();

        try {
            saveUserWithEmail(savedUser.getEmail());
        } catch (Exception e) {
            assertThat(e, instanceOf(DataIntegrityViolationException.class));
        }
    }

    @Test
    public void shouldVerifyTheRetrievalOfUserByUsernameOnFindByUsernameOrEmail() {
        final User savedUser = saveUser();

        final User retrievedUser = userRepository.findByUsernameOrEmail(savedUser.getUsername(), randomAlphanumeric(5, 35)).get();
        assertThat(retrievedUser, new ObjectDeepDetailMatcher(savedUser));
    }

    @Test
    public void shouldVerifyTheRetrievalOfUserByEmailOnFindByUsernameOrEmail() {
        final User savedUser = saveUser();

        final User retrievedUser = userRepository.findByUsernameOrEmail(randomAlphanumeric(5, 35), savedUser.getEmail()).get();
        assertThat(retrievedUser, new ObjectDeepDetailMatcher(savedUser));
    }

    @Test
    public void shouldsdsVerifyTheRetrievalOfUserByEmailOnFindByUsernameOrEmail() {
        final User savedUser = saveUser();

        final User retrievedUser = userRepository.findByUsernameOrEmail(randomAlphanumeric(5, 35), savedUser.getEmail()).get();
        assertThat(retrievedUser, new ObjectDeepDetailMatcher(savedUser));
    }

    @Test
    public void shouldVerifyThatNoUserIsReturnedOnFindByUsernameOrEmailWhenUserDoesNotExistWithBothSpecifiedUsernameAndEmail() {
        final Optional<User> retrievedUser = userRepository.findByUsernameOrEmail(randomAlphanumeric(5, 35), randomAlphanumeric(5, 35));
        assertThat(retrievedUser.isPresent(), is(false));
    }

    private User saveUser() {
        return saveUserWithUsername(randomAlphanumeric(5, 35));
    }

    private User saveUserWithUsername(final String username) {
        final UserBuilder userBuilder = builder()
                .username(username)
                .email(randomAlphanumeric(5) + "@" + randomAlphanumeric(5) + "." + randomAlphanumeric(3));
        return saveUser(userBuilder);
    }

    private User saveUserWithEmail(final String email) {
        final UserBuilder userBuilder = builder()
                .username(randomAlphanumeric(5, 35))
                .email(email);
        return saveUser(userBuilder);
    }

    private User saveUser(final UserBuilder userBuilder) {
        final User user = userBuilder
                .name(randomAlphanumeric(5, 35))
                .password(randomAlphanumeric(5, 35))
                .build();
        user.setRoles(chooseOneOf(getRolesCollection(roleRepository)));

        return userRepository.saveAndFlush(user);
    }
}