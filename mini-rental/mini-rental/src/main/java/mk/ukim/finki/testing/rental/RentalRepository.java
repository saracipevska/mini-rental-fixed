package mk.ukim.finki.testing.rental;

/** Persistence of rentals. */
public interface RentalRepository {
    /**
     * Saves a rental.
     *
     * @param rental the rental to save
     * @throws IllegalStateException if the rental cannot be saved
     */
    void save(Rental rental);
}
