package penetration.pk.lucidxpo.ynami.exceptions;

public class UnexpectedContentException extends RuntimeException {
    public UnexpectedContentException(final String msg) {
        super(msg);
    }

    public UnexpectedContentException(final Throwable cause) {
        super(cause);
    }

    public UnexpectedContentException(final String message,
                                      final Throwable cause,
                                      final boolean enableSuppression,
                                      final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}