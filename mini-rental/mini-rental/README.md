# Mini Rental

This is a plain Java Maven project. You do not need Spring, a database, Docker,
a web server or an internet service. The repository and notification interfaces
represent external dependencies.

## Getting started

1. Extract the archive and open its `pom.xml` in IntelliJ IDEA as a Maven project.
2. Select JDK 17 as the project SDK and Maven runner JDK.
3. Allow Maven to download the dependencies. The first build needs internet access.
4. Run the following commands from the directory containing `pom.xml`.

Windows PowerShell:

```powershell
.\mvnw.cmd -version
.\mvnw.cmd test
```

Linux or macOS:

```bash
chmod +x mvnw
./mvnw -version
./mvnw test
```

The Maven wrapper downloads Maven automatically; a separate Maven installation
is not required. `JAVA_HOME`, if set, must point to your JDK.

## Application behavior

All monetary values are integer cents. For example, 1,250 means 12.50 currency
units. Keep calculations and assertions in cents.

### RentalFeeCalculator

`calculateTotal(dailyRateCents, days, units, member)` calculates a rental quote
containing one equipment type.

| Rule | Required behavior |
| --- | --- |
| Daily rate | Inclusive range 1 through 100,000 cents per unit |
| Days | Inclusive range 1 through 30 |
| Units | Inclusive range 1 through 10 |
| Invalid rate, days or units | Throw `IllegalArgumentException` |
| Subtotal | Daily rate multiplied by days and units |
| Standard customer | No discount |
| Member | Pay 85% of the subtotal, rounded down to whole cents |
| Handling | Add 300 cents once per rental if the subtotal **after discount** is below 10,000 cents |
| No handling fee | Subtotal after discount is at least 10,000 cents |
| Total | Discounted subtotal plus handling fee |

### Equipment

`Equipment` is an immutable Java record. Its code must not be null, empty or
blank. Its daily rate uses the same range as `RentalFeeCalculator`. Available
units must be zero or positive. Invalid constructor arguments throw
`IllegalArgumentException`. Record accessors are `code()`, `dailyRateCents()` and
`availableUnits()`.

### RentalService

`rent(customerId, equipmentCode, days, units, member)` follows these rules:

1. Validate customer ID, equipment code, days and units, in that order. IDs and
   codes must not be null, empty or blank. Days must be between 1 and 30 inclusive;
   units must be between 1 and 10 inclusive. Invalid input throws
   `IllegalArgumentException` before contacting dependencies.
2. Look up the equipment by its exact code once. IDs and codes are used as
   supplied; they are not trimmed or converted to another case.
3. If equipment is missing, throw `EquipmentNotFoundException`.
4. If requested units exceed available units, throw
   `InsufficientAvailabilityException`. Requesting exactly the available units
   is allowed. Availability is a snapshot; this operation does not decrement it
   or reserve equipment for particular dates.
5. Calculate the total using the real `RentalFeeCalculator`.
6. Save the rental once, then send its confirmation once, using the same rental.
7. Return the rental containing the supplied customer ID, equipment code, days,
   units and calculated total.

No rental is saved or confirmation sent for validation, missing-equipment or
availability errors. If saving throws `IllegalStateException`, propagate that
exception and do not send a confirmation. If notification throws
`IllegalStateException`, propagate it; saving has already happened and is not
rolled back.

## Running your tests

Windows PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd test "-Dtest=RentalFeeCalculatorTest"
.\mvnw.cmd test "-Dtest=RentalServiceTest"
.\mvnw.cmd clean verify
.\mvnw.cmd -Pstatic-analysis verify
```

On Linux/macOS, replace `.\mvnw.cmd` with `mvnw`. Test class names should end
with `Test` so Maven discovers them. You can also use IntelliJ's JUnit runner.
The ordinary `test` command runs tests independently of the static-analysis gate.
The `static-analysis` profile enables Checkstyle using `config/checkstyle.xml`
for the final verification. Run it before and after improving the code. Preserve
the documented application behavior when refactoring. Test sources belong under
`src/test/java/mk/ukim/finki/testing/rental`.
