package mk.ukim.finki.testing.rental;

/** Thrown when more units are requested than are available. */
public class InsufficientAvailabilityException extends RuntimeException {
    /**
     * Creates the exception.
     *
     * @param requested the number of units requested
     * @param available the number of units available
     */
    public InsufficientAvailabilityException(int requested, int available) {
        super("Requested units " + requested + " exceeds available units " + available);
    }
}
