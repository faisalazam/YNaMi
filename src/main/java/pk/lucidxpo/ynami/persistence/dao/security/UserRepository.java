package pk.lucidxpo.ynami.persistence.dao.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.lucidxpo.ynami.persistence.model.security.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameOrEmail(String username, String email);
}