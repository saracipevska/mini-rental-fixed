package mk.ukim.finki.testing.rental;

/** Equipment availability is a snapshot, not a reservation. */
public record Equipment(String code, int dailyRateCents, int availableUnits) {
    private static final int MAX_DAILY_RATE_CENTS = 100_000;
    /**
     * Validates the components.
     *
     * @throws IllegalArgumentException if the code is blank, the rate is out of range or the units are negative
     */
    public Equipment {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Equipment code must not be blank");
        }
        if (dailyRateCents < 1 || dailyRateCents > MAX_DAILY_RATE_CENTS) {
            throw new IllegalArgumentException("Daily rate must be between 1 and 100000 cents");
        }
        if (availableUnits < 0) {
            throw new IllegalArgumentException("Available units must not be negative");
        }
    }
}
