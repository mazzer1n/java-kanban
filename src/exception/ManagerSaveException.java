package exception;

import java.io.IOException;

public class ManagerSaveException extends IOException {
    public ManagerSaveException() {

    }

    ManagerSaveException(final String message) {
        super(message);
    }

}
