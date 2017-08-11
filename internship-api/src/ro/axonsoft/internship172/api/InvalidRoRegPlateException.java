package ro.axonsoft.internship172.api;
/**
 * Clasa pentru exceptiile aparute daca numarul de inmatriculare al masinii este invalid
 * @author intern
 *
 */
public class InvalidRoRegPlateException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidRoRegPlateException() {
        super();

    }

    public InvalidRoRegPlateException(final String message) {
        super(message);
    }

}
