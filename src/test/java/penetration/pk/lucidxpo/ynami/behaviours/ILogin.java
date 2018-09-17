package penetration.pk.lucidxpo.ynami.behaviours;

import penetration.pk.lucidxpo.ynami.model.Credentials;

public interface ILogin {

    void login(Credentials credentials);

    void openLoginPage();

    /*
     * Determine whether the user is currently logged in or not.
     * This should involve first making a request for a resource and then determining whether the
     * user is logged in based on the response.
     * To improve robustness, the call to the resource should be possible from any location
     * in the application.
     */
    boolean isLoggedIn();
}