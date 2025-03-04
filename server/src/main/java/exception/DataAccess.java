package exception;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccess extends Exception {
    public DataAccess(String message) {
        super(message);
    }
}
