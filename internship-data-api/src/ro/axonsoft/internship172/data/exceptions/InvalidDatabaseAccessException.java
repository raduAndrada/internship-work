package ro.axonsoft.internship172.data.exceptions;

public class InvalidDatabaseAccessException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidDatabaseAccessException() {
        super();
    }

    public InvalidDatabaseAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidDatabaseAccessException(final String message) {
        super(message);
    }




}
