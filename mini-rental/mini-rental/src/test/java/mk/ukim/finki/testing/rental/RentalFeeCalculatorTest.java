package mk.ukim.finki.testing.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.params.provider.ValueSource;

class RentalFeeCalculatorTest {

    private final RentalFeeCalculator calculator = new RentalFeeCalculator();

    @ParameterizedTest
    @CsvSource({
            "1,     1, 1,   301",   // 1 + 300 handling
            "100,   1, 1,   400",   // 100 + 300 handling
            "3333,  3, 1, 10299",   // subtotal 9,999, still below 10,000
            "5000,  2, 1, 10000"    // subtotal exactly 10,000, no handling
    })
    void standardCustomer(int rate, int days, int units, int expected) {
        assertEquals(expected, calculator.calculateTotal(rate, days, units, false));
    }

    @ParameterizedTest
    @CsvSource({
            "1,      1, 1,   300",  // 85% of 1 rounds down to 0, + 300
            "100,    1, 1,   385",  // 85 + 300
            "10000,  1, 1,  8800",  // discounted 8,500 is below 10,000, so + 300
            "11765,  1, 1, 10000"   // discounted 10,000, no handling
    })
    void member(int rate, int days, int units, int expected) {
        assertEquals(expected, calculator.calculateTotal(rate, days, units, true));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 100_001})
    void invalidRate(int rate) {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateTotal(rate, 2, 3, false));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 31})
    void invalidDays(int days) {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateTotal(1_000, days, 3, false));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11})
    void invalidUnits(int units) {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateTotal(1_000, 2, units, false));
    }
}