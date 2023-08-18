package exception;

public class InterruptedException extends RuntimeException {
    public InterruptedException() {

    }

    InterruptedException(final String message) {
        super(message);
    }

}