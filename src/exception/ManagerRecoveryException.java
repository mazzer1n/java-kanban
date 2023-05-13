package exception;

import java.io.IOException;

public class ManagerRecoveryException extends IOException {
    public ManagerRecoveryException() {

    }

    ManagerRecoveryException(final String message) {
        super(message);
    }

}

