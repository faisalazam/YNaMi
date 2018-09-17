package penetration.pk.lucidxpo.ynami.web;

import penetration.pk.lucidxpo.ynami.clients.AuthTokenManager;

import static java.lang.Thread.sleep;

public abstract class Application {
    public void pause(final long milliSeconds) {
        try {
            sleep(milliSeconds);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void enableDefaultClient();

    public abstract void enableHttpLoggingClient();

    public abstract AuthTokenManager getAuthTokenManager();
}

