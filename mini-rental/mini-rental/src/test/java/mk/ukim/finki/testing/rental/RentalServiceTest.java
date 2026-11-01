package mk.ukim.finki.testing.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalNotifier notifier;

    private RentalService service;

    @BeforeEach
    void setUp() {
        service = new RentalService(equipmentRepository, rentalRepository, notifier, new RentalFeeCalculator());
    }

    @Test
    void successfulRental() {
        when(equipmentRepository.findByCode("drill")).thenReturn(Optional.of(new Equipment("drill", 1_250, 5)));

        Rental result = service.rent("c1", "drill", 3, 2, false);

        assertEquals(7_800, result.totalCents());
        verify(rentalRepository).save(result);
        verify(notifier).sendConfirmation(result);
    }

    @Test
    void equipmentNotFound() {
        when(equipmentRepository.findByCode("x")).thenReturn(Optional.empty());

        assertThrows(EquipmentNotFoundException.class, () -> service.rent("c1", "x", 1, 1, false));
        verifyNoInteractions(rentalRepository, notifier);
    }

    @Test
    void notEnoughUnits() {
        when(equipmentRepository.findByCode("drill")).thenReturn(Optional.of(new Equipment("drill", 1_000, 2)));

        assertThrows(InsufficientAvailabilityException.class, () -> service.rent("c1", "drill", 1, 3, false));
        verifyNoInteractions(rentalRepository, notifier);
    }

    @Test
    void exactlyAvailableUnitsIsAllowed() {
        when(equipmentRepository.findByCode("drill")).thenReturn(Optional.of(new Equipment("drill", 500, 4)));

        Rental result = service.rent("c1", "drill", 1, 4, false);

        assertEquals(4, result.units());
        verify(rentalRepository).save(result);
    }

    @ParameterizedTest
    @CsvSource({"false, 7800", "true, 6675"})
    void totalDependsOnMembership(boolean member, int expected) {
        when(equipmentRepository.findByCode("drill")).thenReturn(Optional.of(new Equipment("drill", 1_250, 10)));

        Rental result = service.rent("c1", "drill", 3, 2, member);

        assertEquals(expected, result.totalCents());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void blankCustomerIdRejected(String customerId) {
        assertThrows(IllegalArgumentException.class, () -> service.rent(customerId, "drill", 1, 1, false));
        verifyNoInteractions(equipmentRepository, rentalRepository, notifier);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void blankEquipmentCodeRejected(String code) {
        assertThrows(IllegalArgumentException.class, () -> service.rent("c1", code, 1, 1, false));
        verifyNoInteractions(equipmentRepository, rentalRepository, notifier);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 31})
    void invalidDaysRejected(int days) {
        assertThrows(IllegalArgumentException.class, () -> service.rent("c1", "drill", days, 1, false));
        verifyNoInteractions(equipmentRepository, rentalRepository, notifier);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11})
    void invalidUnitsRejected(int units) {
        assertThrows(IllegalArgumentException.class, () -> service.rent("c1", "drill", 1, units, false));
        verifyNoInteractions(equipmentRepository, rentalRepository, notifier);
    }

    @Test
    void saveFailureDoesNotSendConfirmation() {
        when(equipmentRepository.findByCode("drill")).thenReturn(Optional.of(new Equipment("drill", 500, 5)));
        doThrow(new IllegalStateException("db down")).when(rentalRepository).save(any(Rental.class));

        assertThrows(IllegalStateException.class, () -> service.rent("c1", "drill", 1, 1, false));
        verifyNoInteractions(notifier);
    }

    @Test
    void notificationFailureKeepsSavedRental() {
        when(equipmentRepository.findByCode("drill")).thenReturn(Optional.of(new Equipment("drill", 500, 5)));
        doThrow(new IllegalStateException("mail down")).when(notifier).sendConfirmation(any(Rental.class));

        assertThrows(IllegalStateException.class, () -> service.rent("c1", "drill", 1, 1, false));
        verify(rentalRepository).save(any(Rental.class));
    }
}