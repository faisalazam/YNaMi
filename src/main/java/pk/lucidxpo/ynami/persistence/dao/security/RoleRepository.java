package pk.lucidxpo.ynami.persistence.dao.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.lucidxpo.ynami.persistence.model.security.Role;
import pk.lucidxpo.ynami.persistence.model.security.RoleName;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}