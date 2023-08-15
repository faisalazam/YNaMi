package it.pk.lucidxpo.ynami.persistence.dao.security;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.persistence.dao.security.RoleRepository;
import pk.lucidxpo.ynami.persistence.dao.security.UserRepository;
import pk.lucidxpo.ynami.persistence.model.security.Role;
import pk.lucidxpo.ynami.persistence.model.security.User;
import pk.lucidxpo.ynami.persistence.model.security.UserBuilder;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.util.Optional;
import java.util.Set;

import static it.pk.lucidxpo.ynami.AbstractIntegrationTest.ADMIN_USER;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static pk.lucidxpo.ynami.persistence.model.security.UserBuilder.anUser;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;
import static pk.lucidxpo.ynami.utils.matchers.ObjectDeepDetailMatcher.equivalentTo;

@Transactional
@WithUserDetails(value = ADMIN_USER)
@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldVerifyThatUserIsPersistedWithMultipleRoles() {
        for (final Set<Role> associatedRoles : getRolesCollection(roleRepository)) {
            final String username = randomAlphanumeric(5, 35);
            final String email = randomAlphanumeric(5) + "@" + randomAlphanumeric(5) + "." + randomAlphanumeric(3);
            final User userWithSpecifiedUserName = anUser()
                    .withUsername(username)
                    .withEmail(email)
                    .build();

            userWithSpecifiedUserName.setRoles(associatedRoles);

            final User savedUser = userRepository.save(userWithSpecifiedUserName);
            assertThat(savedUser.getName(), is(userWithSpecifiedUserName.getName()));
            assertThat(savedUser.getUsername(), is(userWithSpecifiedUserName.getUsername()));
            assertThat(savedUser.getEmail(), is(userWithSpecifiedUserName.getEmail()));
            assertThat(savedUser.getPassword(), is(userWithSpecifiedUserName.getPassword()));

            final Set<Role> savedUserRoles = savedUser.getRoles();
            assertThat(savedUserRoles.isEmpty(), is(false));
            assertThat(savedUserRoles.containsAll(associatedRoles), is(true));
        }
    }

    @Test
    void shouldVerifyThatUserWithNonUniqueUsernameDoesNotGetPersisted() {
        final User savedUser = saveUser();

        try {
            saveUserWithUsername(savedUser.getUsername());
        } catch (final Exception e) {
            assertThat(e, instanceOf(DataIntegrityViolationException.class));
        }
    }

    @Test
    void shouldVerifyThatUserWithNonUniqueEmailDoesNotGetPersisted() {
        final User savedUser = saveUser();

        try {
            saveUserWithEmail(savedUser.getEmail());
        } catch (final Exception e) {
            assertThat(e, instanceOf(DataIntegrityViolationException.class));
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void shouldVerifyTheRetrievalOfUserByUsernameOnFindByUsernameOrEmail() {
        final User savedUser = saveUser();

        final User retrievedUser = userRepository.findByUsernameOrEmail(savedUser.getUsername(), randomAlphanumeric(5, 35)).get();
        assertThat(retrievedUser, equivalentTo(savedUser));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void shouldVerifyTheRetrievalOfUserByEmailOnFindByUsernameOrEmail() {
        final User savedUser = saveUser();

        final User retrievedUser = userRepository.findByUsernameOrEmail(randomAlphanumeric(5, 35), savedUser.getEmail()).get();
        assertThat(retrievedUser, equivalentTo(savedUser));
    }

    @Test
    void shouldVerifyThatNoUserIsReturnedOnFindByUsernameOrEmailWhenUserDoesNotExistWithBothSpecifiedUsernameAndEmail() {
        final Optional<User> retrievedUser = userRepository.findByUsernameOrEmail(randomAlphanumeric(5, 35), randomAlphanumeric(5, 35));
        assertThat(retrievedUser.isPresent(), is(false));
    }

    private User saveUser() {
        return saveUserWithUsername(randomAlphanumeric(5, 35));
    }

    private User saveUserWithUsername(final String username) {
        final UserBuilder userBuilder = anUser().withUsername(username);
        return saveUser(userBuilder);
    }

    @SuppressWarnings("UnusedReturnValue")
    private User saveUserWithEmail(final String email) {
        final UserBuilder userBuilder = anUser().withEmail(email);
        return saveUser(userBuilder);
    }

    private User saveUser(final UserBuilder userBuilder) {
        final User user = userBuilder
                .withRoles(chooseOneOf(getRolesCollection(roleRepository)))
                .build();

        return userRepository.saveAndFlush(user);
    }
}