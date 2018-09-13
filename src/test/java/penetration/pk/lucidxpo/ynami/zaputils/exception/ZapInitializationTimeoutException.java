package penetration.pk.lucidxpo.ynami.zaputils.exception;

/**
 * This exception should be thrown if ZAP is not started before the specified timeout.
 */
public class ZapInitializationTimeoutException extends RuntimeException {

    private static final long serialVersionUID = -5283245793671447701L;

    public ZapInitializationTimeoutException(final String message) {
        super(message);
    }

    public ZapInitializationTimeoutException(final Throwable e) {
        super(e);
    }

    public ZapInitializationTimeoutException(final String message, final Throwable e) {
        super(message, e);
    }

}
