package mk.ukim.finki.testing.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EquipmentTest {

    @Test
    void validEquipment() {
        Equipment e = new Equipment("drill", 1_250, 5);
        assertEquals("drill", e.code());
        assertEquals(1_250, e.dailyRateCents());
        assertEquals(5, e.availableUnits());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void blankCodeRejected(String code) {
        assertThrows(IllegalArgumentException.class, () -> new Equipment(code, 1_000, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 100_001})
    void invalidRateRejected(int rate) {
        assertThrows(IllegalArgumentException.class, () -> new Equipment("drill", rate, 1));
    }

    @Test
    void negativeUnitsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Equipment("drill", 1_000, -1));
    }

    @Test
    void zeroUnitsAllowed() {
        assertEquals(0, new Equipment("drill", 1_000, 0).availableUnits());
    }
}