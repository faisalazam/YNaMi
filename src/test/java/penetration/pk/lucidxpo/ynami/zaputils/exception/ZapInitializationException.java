package penetration.pk.lucidxpo.ynami.zaputils.exception;

/**
 * Exception thrown when ZAP initialization fails.
 */
public class ZapInitializationException extends RuntimeException {

    private static final long serialVersionUID = -2305184594319127381L;

    public ZapInitializationException(final String message) {
        super(message);
    }

    public ZapInitializationException(final Throwable e) {
        super(e);
    }

    public ZapInitializationException(final String message, final Throwable e) {
        super(message, e);
    }

}
