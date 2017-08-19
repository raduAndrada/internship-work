package ro.axonsoft.internship172.model.api;

/**
 * Clasa care defineste exceptiile aparute daca id cardului de identitate este
 * invalid
 *
 * @author intern
 *
 */
public class InvalidRoIdCardException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidRoIdCardException() {
        super("Invalid Ro Id Card");

    }

    public InvalidRoIdCardException(final String message) {
        super(message);

    }

    public InvalidRoIdCardException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
