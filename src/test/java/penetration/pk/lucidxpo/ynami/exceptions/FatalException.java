package penetration.pk.lucidxpo.ynami.exceptions;

import static java.lang.System.exit;

class FatalException extends RuntimeException {
    public FatalException(final String msg) {
        super(msg);
        exit(1);
    }
}