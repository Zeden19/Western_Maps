package cs2212.westernmaps.help;

/**
 * Signals that an error occurred while loading help, usually because of
 * invalid, missing, or corrupted help files.
 *
 * <p>This error is typically caused by an issue in the project's build system
 * or packaging system that causes invalid help files to be generated.</p>
 *
 * @author Connor Cummings
 */
public final class InvalidHelpException extends RuntimeException {
    /**
     * Create a new {@link InvalidHelpException} that was caused by another
     * exception.
     *
     * @param inner The exception that caused the error.
     */
    public InvalidHelpException(Throwable inner) {
        super("Help could not be loaded", inner);
    }

    /**
     * Create a new {@link InvalidHelpException} with a custom message.
     *
     * @param message The message to display.
     */
    public InvalidHelpException(String message) {
        super(message);
    }
}
