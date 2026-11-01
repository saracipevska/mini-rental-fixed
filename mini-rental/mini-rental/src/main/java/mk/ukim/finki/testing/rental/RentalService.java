package mk.ukim.finki.testing.rental;

import java.util.Objects;


/** Creates rentals: validates, checks availability, prices, saves and confirms. */
public final class RentalService {
    private final EquipmentRepository equipment;
    private final RentalRepository rentals;
    private final RentalNotifier notifier;
    private final RentalFeeCalculator calculator;

    /**
     * Creates the service.
     *
     * @param equipment equipment catalogue
     * @param rentals rental storage
     * @param notifier confirmation sender
     * @param calculator fee calculator
     */
    public RentalService(EquipmentRepository equipment, RentalRepository rentals,
                         RentalNotifier notifier, RentalFeeCalculator calculator) {
        this.equipment = Objects.requireNonNull(equipment);
        this.rentals = Objects.requireNonNull(rentals);
        this.notifier = Objects.requireNonNull(notifier);
        this.calculator = Objects.requireNonNull(calculator);
    }

    private static final int MAX_DAYS = 30;
    private static final int MAX_UNITS = 10;

    /**
     * Rents equipment to a customer.
     *
     * @param customerId non-blank customer ID
     * @param equipmentCode non-blank equipment code
     * @param days rental length (1 to 30)
     * @param units number of units (1 to 10)
     * @param member whether the customer gets the member discount
     * @return the saved rental
     * @throws IllegalArgumentException if any argument is invalid
     * @throws EquipmentNotFoundException if the equipment does not exist
     * @throws InsufficientAvailabilityException if more units are requested than available
     */
    public Rental rent(String customerId, String equipmentCode, int days, int units, boolean member) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID must not be blank");
        }
        if (equipmentCode == null || equipmentCode.isBlank()) {
            throw new IllegalArgumentException("Equipment code must not be blank");
        }
        if (days < 1 || days > MAX_DAYS) {
            throw new IllegalArgumentException("Days must be between 1 and 30");
        }
        if (units < 1 || units > MAX_UNITS) {
            throw new IllegalArgumentException("Units must be between 1 and 10");
        }
        Equipment item = equipment.findByCode(equipmentCode)
                .orElseThrow(() -> new EquipmentNotFoundException(equipmentCode));
        if (units > item.availableUnits()) {
            throw new InsufficientAvailabilityException(units, item.availableUnits());
        }
        int total = calculator.calculateTotal(item.dailyRateCents(), days, units, member);
        Rental rental = new Rental(customerId, equipmentCode, days, units, total);
        rentals.save(rental);
        notifier.sendConfirmation(rental);
        return rental;
    }
}
