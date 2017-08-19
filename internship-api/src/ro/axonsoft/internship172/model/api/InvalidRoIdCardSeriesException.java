package ro.axonsoft.internship172.model.api;
/**
 * Clasa care defineste exceptiile pentru seria cartii de identitate invalida
 * @author intern
 *
 */
public class InvalidRoIdCardSeriesException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidRoIdCardSeriesException() {
        super("Invalid Ro Id Card");

    }

    public InvalidRoIdCardSeriesException(final String message) {
        super(message);

    }
}
