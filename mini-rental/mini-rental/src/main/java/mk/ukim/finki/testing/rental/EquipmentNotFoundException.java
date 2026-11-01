package mk.ukim.finki.testing.rental;

/** Thrown when no equipment exists for the requested code. */
public class EquipmentNotFoundException extends RuntimeException {
    /**
     * Creates the exception.
     *
     * @param code the equipment code that was not found
     */
    public EquipmentNotFoundException(String code) {
        super("Equipment not found: " + code);
    }
}
