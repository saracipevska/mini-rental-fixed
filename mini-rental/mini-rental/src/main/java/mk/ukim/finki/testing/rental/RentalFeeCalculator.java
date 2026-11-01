package mk.ukim.finki.testing.rental;

/** Calculates a rental quote for one equipment type. All amounts are cents. */
public final class RentalFeeCalculator {

    private static final int MAX_DAILY_RATE_CENTS = 100_000;
    private static final int MAX_DAYS = 30;
    private static final int MAX_UNITS = 10;
    private static final int MEMBER_PERCENT = 85;
    private static final int PERCENT_DIVISOR = 100;
    private static final int HANDLING_FEE_CENTS = 300;
    private static final int HANDLING_FEE_THRESHOLD_CENTS = 10_000;

    /**
     * Calculates the total price of a rental.
     *
     * @param dailyRateCents daily rate per unit in cents (1 to 100,000)
     * @param days number of rental days (1 to 30)
     * @param units number of rented units (1 to 10)
     * @param member whether the customer is a member and gets the discount
     * @return the total price in cents
     * @throws IllegalArgumentException if the rate, days or units are out of range
     */


    public int calculateTotal(int dailyRateCents, int days, int units, boolean member) {
        if (dailyRateCents < 1 || dailyRateCents > MAX_DAILY_RATE_CENTS) {
            throw new IllegalArgumentException("Daily rate must be between 1 and 100000 cents");
        }
        if (days < 1 || days > MAX_DAYS) {
            throw new IllegalArgumentException("Days must be between 1 and 30");
        }
        if (units < 1 || units > MAX_UNITS) {
            throw new IllegalArgumentException("Units must be between 1 and 10");
        }
        int subtotal = dailyRateCents * days * units;
        int rentalFee;
        if (member) {
            rentalFee = (int) (subtotal * (long) MEMBER_PERCENT / PERCENT_DIVISOR);
        } else {
            rentalFee = subtotal;
        }
        int handlingFee = HANDLING_FEE_CENTS;
        if (rentalFee >= HANDLING_FEE_THRESHOLD_CENTS) {
            handlingFee = 0;
        }
        return rentalFee + handlingFee;
    }
}
