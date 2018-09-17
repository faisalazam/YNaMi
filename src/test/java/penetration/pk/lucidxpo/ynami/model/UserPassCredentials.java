package penetration.pk.lucidxpo.ynami.model;

public class UserPassCredentials extends Credentials {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    public UserPassCredentials(final Credentials credentials) {
        super(USERNAME, credentials.get(USERNAME), PASSWORD, credentials.get(PASSWORD));
    }

    public UserPassCredentials(final String username, final String password) {
        super(USERNAME, username, PASSWORD, password);
    }

    public String getUsername() {
        return credentials.get(USERNAME);
    }

    public void setUsername(final String username) {
        this.credentials.put(USERNAME, username);
    }

    public String getPassword() {
        return credentials.get(PASSWORD);
    }

    public void setPassword(final String password) {
        this.credentials.put(PASSWORD, password);
    }
}