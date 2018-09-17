package penetration.pk.lucidxpo.ynami.clients;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;
import static org.openqa.selenium.Cookie.Builder;
import static penetration.pk.lucidxpo.ynami.config.Config.getInstance;

@Getter
@Setter
public class AuthTokenManagerImpl implements AuthTokenManager {
    private WebDriver driver;

    public AuthTokenManagerImpl(final WebDriver driver) {
        this.driver = driver;
    }

    public Cookie getCookieByName(final String name) {
        return this.driver.manage().getCookieNamed(name);
    }

    public void getUrl(final String url) {
        driver.get(url);
    }

    @Override
    public Map<String, String> getAuthTokens() {
        return getInstance().getSessionIDs().stream()
                .map(name -> driver.manage().getCookieNamed(name))
                .filter(Objects::nonNull)
                .collect(toMap(Cookie::getName, Cookie::getValue, (a, b) -> b));
    }

    @Override
    public void setAuthTokens(final Map<String, String> tokens) {
        tokens.keySet().forEach(name -> {
            driver.manage().deleteCookieNamed(name);
            driver.manage().addCookie(new Builder(name, tokens.get(name)).build());
        });
    }

    @Override
    public void deleteAuthTokens() {
        driver.manage().deleteAllCookies();
    }
}