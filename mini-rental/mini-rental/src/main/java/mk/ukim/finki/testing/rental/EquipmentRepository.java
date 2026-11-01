package mk.ukim.finki.testing.rental;

import java.util.Optional;

/** Read access to the equipment catalogue. */
public interface EquipmentRepository {
    /**
     * Finds equipment by its exact code.
     *
     * @param code the equipment code
     * @return the equipment, or {@code Optional.empty()} when the code does not exist
     */
    Optional<Equipment> findByCode(String code);
}

