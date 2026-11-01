package mk.ukim.finki.testing.rental;

/** Immutable rental data. All monetary amounts are cents. */
public record Rental(String customerId, String equipmentCode, int days, int units, int totalCents) {
}
