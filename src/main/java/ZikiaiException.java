/**
 * Represents an error caused by an invalid command entered in Zikiai.
 */
public class ZikiaiException extends Exception {

    /**
     * Creates an exception with a message that explains the input error.
     *
     * @param message explanation shown to the user
     */
    public ZikiaiException(String message) {
        super(message);
    }
}
