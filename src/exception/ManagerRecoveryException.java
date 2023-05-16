package exception;

import java.io.IOException;

public class ManagerRecoveryException extends RuntimeException {
    public ManagerRecoveryException() {

    }

    ManagerRecoveryException(final String message) {
        super(message);
    }

}

