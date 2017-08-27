package ro.axonsoft.internship172.data.exceptions;

public class DatabaseIntegrityViolationException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DatabaseIntegrityViolationException() {
        super();
    }

    public DatabaseIntegrityViolationException(final String message) {
        super(message);
    }

    public DatabaseIntegrityViolationException(final Throwable cause) {
        super(cause);
    }



}
