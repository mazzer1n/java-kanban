package exception;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {

    }

    ManagerSaveException(final String message) {
        super(message);
    }

}
