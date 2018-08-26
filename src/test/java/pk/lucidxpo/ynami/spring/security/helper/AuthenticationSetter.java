package pk.lucidxpo.ynami.spring.security.helper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import pk.lucidxpo.ynami.spring.security.UserPrincipal;

import static org.springframework.security.test.context.TestSecurityContextHolder.getContext;
import static pk.lucidxpo.ynami.persistence.model.security.UserBuilder.anUser;
import static pk.lucidxpo.ynami.spring.security.UserPrincipal.create;

public class AuthenticationSetter {
    public static UserDetails setupAuthentication() {
        final UserPrincipal userPrincipal = create(anUser().build());
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.getPassword(), userPrincipal.getAuthorities());
        getContext().setAuthentication(authentication);
        return userPrincipal;
    }
}