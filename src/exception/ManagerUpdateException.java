package exception;

public class ManagerUpdateException extends RuntimeException {
    public ManagerUpdateException () {}

    public ManagerUpdateException (final String message) {
        super(message);
    }
}
