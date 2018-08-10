package pk.lucidxpo.ynami.persistence.dao.security;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.model.security.Role;
import pk.lucidxpo.ynami.persistence.model.security.User;
import pk.lucidxpo.ynami.utils.matchers.ObjectDeepDetailMatcher;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.persistence.model.security.RoleName.values;
import static pk.lucidxpo.ynami.persistence.model.security.User.UserBuilder;
import static pk.lucidxpo.ynami.persistence.model.security.User.builder;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@Transactional
public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldVerifyThatUserIsPersistedWithAuditInfoAndMultipleRoles() {
        for (Set<Role> associatedRoles : getRolesCollection()) {
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
        user.setRoles(chooseOneOf(getRolesCollection()));

        return userRepository.saveAndFlush(user);
    }

    /*
     * This method will return a collection of 'Set<Role>', where each 'Set<Role>' will have different size.
     */
    private Collection<Set<Role>> getRolesCollection() {
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