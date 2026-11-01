package mk.ukim.finki.testing.rental;

/** Sends rental confirmations. */
public interface RentalNotifier {
    /**
     * Sends a confirmation for a rental.
     *
     * @param rental the rental to confirm
     * @throws IllegalStateException if the confirmation cannot be delivered
     */
    void sendConfirmation(Rental rental);
}
