# Akosua's Pharmacy Tracker (rebuild)

A rebuild of the pharmacy's stock and dispensing tracker. It behaves like the original, except that **stock can never go negative**, and the structure now makes new medicine categories (controlled substances next) an addition, not an edit.

- Written diagnosis and design sketch: [DIAGNOSIS.md](DIAGNOSIS.md) (committed before the first code change)
- Original output (evidence): `baseline-output.txt`

## How to run

Requirements: **JDK 21 or newer** and Maven. The project compiles with `--release 21`.

```
mvn test                 # runs the golden-output test and the unit tests
mvn -q compile
java -cp target/classes com.akosua.pharmacy.app.PharmacyTracker
```

### Reproducing the test results

Run `mvn test` from the project root. All 18 tests pass:

```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
```

## Structure

| Package | Classes | Purpose |
|---|---|---|
| `app` | `PharmacyTracker` | Entry point and demo. Nothing depends on it |
| `model` | `Medication`, `Money`, `Customer` (interface), `WalkInCustomer`, `RegisteredPatient`, `Transaction` (record), `TransactionType` (enum), `InsufficientStockException` | The things the pharmacy deals with, each protecting its own data |
| `rules` | `DispensingRule` (interface), `OtcRule`, `PrescriptionRule`, `ControlledRule`, `DispenseRequest` (record), `DispenseRejectedException` | What differs per kind of medicine |
| `service` | `Pharmacy` | The fixed dispense flow and the audit trail |

**Why these packages?** `model` holds concepts that carry data and enforce rules about their own data. `service` coordinates work across them. `app` only wires things together, so launching the program is separate from the logic. `rules` exists because the rules are the part that grows. Dependencies run one way: `model` uses `rules`, and `rules` never imports `model`. `DispenseRequest` carries a medicine **name**, not a `Medication`, to avoid a circular dependency.

## How the stock rule is enforced

- `Medication.stock` is `private`. There is no setter. It changes only through `reduceStock` and `increaseStock`.
- `reduceStock` rejects quantities of zero or less (`IllegalArgumentException`) and quantities above the current stock (`InsufficientStockException`). The stock is only subtracted after both checks pass.
- `increaseStock` rejects non-positive quantities and uses `Math.addExact`, so an `int` overflow cannot silently wrap stock to a negative number. The constructor also rejects a negative starting stock, so both entry points into the stock value are guarded against overflow and invalid input together.
- The constructor rejects a blank name, a negative starting stock, a null price and a null rule.
- In `Pharmacy.dispense`, everything that can fail (rule check, pricing, stock reduction) sits inside a `try`. Announcing, recording history and recording the transaction come **after** it. A rejection throws, skips those lines, and so leaves no phantom record.
- `IllegalArgumentException` is deliberately not caught in `Pharmacy`. It signals a programming error (a negative quantity, a null argument), not a business event. It surfaces as a stack trace in the console, which is intentional: a programming error should be loud and visible so it gets fixed, not swallowed behind a friendly message.

## Key design decisions (chose / rejected / why)

1. **Composition for medicine rules, not subclasses.** *Chose:* `Medication` has a `DispensingRule`. *Rejected:* `OtcMedication` / `PrescriptionMedication` subclasses. *Why:* medicines share one shape and only the rule varies. Subclasses tempt a chain (Controlled extends Prescription extends Medication) where a change in the middle ripples down, and they use Java's single parent slot. *Trade-off:* subclasses would be acceptable for three categories. Composition wins as categories multiply.
2. **Interface only, not an enum holding rule objects, and never an enum plus `switch`.** *Why:* each rule is its own class that supplies its own label, so a new category is a new file with no edit to existing rule code. An enum plus `switch` is the original problem in new clothes. *Cost:* the list of categories is not visible in one place.
3. **The rule interface answers three questions:** what label goes in the report, whether the request is acceptable, and what confirmation text follows the customer name. The third one preserves an original quirk: an OTC line never says "prescribed by", even if a prescriber is supplied.
4. **Plain interface, not sealed, for customers.** Walk-in and registered patients differ in shape (only registered has a phone and a history). I kept the interface open, because new customer types (for example senior or insurance discounts) are plausible. *Trade-off:* sealed would give compiler checking of every case. *Cost of the interface:* each class declares its own `name` field.
5. **Enum only for a fixed set with no behaviour.** `TransactionType` (DISPENSE, RESTOCK) is an enum. Medicine category is not, because it is about to grow and carries behaviour.
6. **Exceptions to refuse invalid changes.** A rejection cannot be silently ignored, unlike a returned result. `IllegalArgumentException` means the caller passed nonsense (a programming error, deliberately not caught). `DispenseRejectedException` and its subclass `InsufficientStockException` mean a normal business "no", which `Pharmacy` catches in one place, prints, and records nothing for. Both are unchecked, so no `throws` clauses spread through the code.
7. **Records only for immutable facts.** `Transaction`, `DispenseRequest` and `Money` are records. `Medication` is not, because its stock changes over time.
8. **`Money` (BigDecimal) instead of `double`.** Exact decimal arithmetic. It is always built from a String, never from a double, because constructing `BigDecimal` from a `double` carries the binary rounding error straight in (`new BigDecimal(0.95)` stores `0.9499999...`, not `0.95`). Display reproduces the original format (`25.0`, not `25.00` or `25`). *No rounding added*, because rounding to pesewas would change some output values and is a second deliberate behaviour change. The 5% discount can still produce three decimals, as it did in the original. This is a known quirk preserved intentionally. Rounding to two decimal places is a documented next step.
9. **Aliasing protection.** `getTransactions()` returns an unmodifiable view, so a caller cannot clear or edit the audit trail. `RegisteredPatient.getHistory()` does the same. The history is never written to logs or persisted beyond the process, so no patient data leaves the application. *Chose a view over a copy:* it is cheaper and always current.
10. **`Pharmacy` owns transaction ids,** with one private counter incremented in one place. It replaced a mutable static counter in each class that anyone could reset.
11. **Delegation inside `ControlledRule`.** It holds a `PrescriptionRule` and passes validation to it, so "controlled is at least as strict as prescription" is written in the code and the prescriber message is not duplicated.

## Controlled substances: the extensibility evidence

- **Rebuilt design:** one new production file (`ControlledRule`) and one test. **Existing production files modified: 0** (verify with `git log --oneline` and inspect the commit that adds `ControlledRule`).
- **The original would have forced:** a third `else if` branch in `Pharmacy.dispense`, copies of the pricing, discount, stock and print lines a third time, a new category string compared in several places, and edits to the report. Every one of those edits is inside code that already works, which is how past changes broke other categories.
- **Prescriber recorded on every controlled dispense:** the rule rejects a null or blank prescriber, and `Transaction` stores the prescriber for every dispense. `ControlledRule` is deliberately thin today. If the law later requires a quantity limit, only `ControlledRule` changes.

## Behaviour preservation

Evidence is a golden-output test: it runs `main` and compares the output with a stored file. `baseline-output.txt` holds the **original** output and is never edited. `src/test/resources/expected-after-fix.txt` is the output after the deliberate changes below.

Original output, the part that changes:

```
Dispensed 100 x Amoxicillin 250mg to Kofi Mensah (prescribed by Dr. Boateng) for GHS 3000.0
...
  Amoxicillin 250mg (PRESCRIPTION): -61 in stock
Transactions today: 4
```

Output after the rebuild:

```
Cannot dispense 100 units of Amoxicillin 250mg: only 39 in stock
...
  Amoxicillin 250mg (PRESCRIPTION): 39 in stock
Transactions today: 3
Rejected dispenses today: 1
  REJECTED: Amoxicillin 250mg - Cannot dispense 100 units of Amoxicillin 250mg: only 39 in stock
```

Reproduce the full diff: `git diff --no-index baseline-output.txt src/test/resources/expected-after-fix.txt`. The first lines (both successful dispenses and the restock) are identical.

**Two deliberate changes, nothing else:**

1. **The fix.** The 100-unit over-dispense is rejected, so stock stays at 39 (not -61) and only 3 transactions are recorded (not 4).
2. **The improvement.** The report gains the rejected-dispense lines (see below).

Until the golden test file was updated for these two changes, the test stayed green through every refactoring step (packages, enum, rules, customers, record, money, split), which is how I know those steps preserved behaviour.

## Improvement beyond the brief: rejected-dispense log, backed by a unit test suite

**Rejected-dispense log.** `Pharmacy` keeps a separate, read-only list of every refused dispense (medicine and reason), and the end-of-day report lists them. *Why it matters here:* the bug that eroded Akosua's trust was a count that was silently wrong. A rejection is exactly the event that used to be invisible, and a pharmacy needs to see "someone tried to dispense 100 and we only had 39". Rejected dispenses are printed to the console and included in the end-of-day report. They are not persisted beyond the process. For a production system, writing them to a persistent audit log would be the next step, since a rejection log that vanishes on exit gives weak assurance to a pharmacist reviewing the day's activity.

**Unit tests (18 in total, all passing):**

| Test class | Tests | What it protects |
|---|---|---|
| `GoldenOutputTest` | 1 | Whole-program output |
| `MedicationTest` | 10 | Stock invariant, constructor validation (blank name, null price, null rule, negative stock), `reduceStock` rejection on zero quantity, `reduceStock` rejection on insufficient stock, `increaseStock` rejection on zero quantity, `Math.addExact` overflow guard, successful reduction, successful increase |
| `ControlledRuleTest` | 3 | New category addition, prescriber requirement, label text |
| `PharmacyTest` | 4 | Rejected dispenses recorded separately, transaction count after rejection, audit trail unmodifiable, rejection list read-only |

*Why tests:* the brief says changes to one kind of medicine "quietly broke another". The golden test says the output changed. Unit tests say which rule broke, within seconds.

## Behaviour decisions

- **Blank prescriber is now rejected.** The original only rejected `null`, so `""` counted as a prescriber. I decided "without a prescriber" means "without a real one". This is a small hardening beyond the one fix. The baseline never exercises it.
- **Walk-in customers no longer keep a history list.** The original did, but nothing ever read or printed it, and the business keeps history only for registered patients. The output is unchanged.
- **Registered patients must have a phone number,** passed to the constructor. The original set it from `main` after construction, which is the outside access that encapsulation removes.
- **Customer ids were dropped.** Nothing read them, and per-class counters would have collided.
- **The transaction stores whatever prescriber the caller supplied,** so an OTC sale with a prescriber name passed in is recorded with it. Only rules that require a prescriber verify it.
- **Unknown categories cannot exist now,** so that bug is gone by construction.

## Design changes from DIAGNOSIS.md

- The sketch showed a separate `Stock` class. I kept stock as a guarded `int` inside `Medication`, because a separate class added nothing.
- `DispensingRule` has three methods (`label`, `validate`, `confirmationSuffix`), not the two in the sketch.
- I added a `DispenseRejectedException` base class so `Pharmacy` needs a single `catch`.
- `Customer` gained `recordDispense`, and the discount method is `applyDiscount`.
- `Transaction` uses `medName` and `qty`, and records the prescriber.

## Known limitations

- **Duplicates are not prevented.** `addMedication` and `addCustomer` accept the same item twice, and `dispense` accepts a medicine that was never added to the pharmacy. This was in the original and is not fixed here. The fix would be to check before adding and throw `IllegalArgumentException`. It was left out to avoid risk near the deadline, and is listed so the next developer knows.
- **Transactions store names, not references,** so two medicines with the same name would be ambiguous.
- **`Medication` ids still come from a static counter,** private now, but not thread-safe.
- **No rounding to pesewas.** Totals can carry more than two decimals. The 5% discount can produce three-decimal results, as it did in the original. Rounding to two decimal places (pesewas) is the natural next step.
- **Rejected dispenses are not persisted.** They appear in the console and the end-of-day report but are lost when the process exits.
- **Test gaps:** no tests yet for `Money`, `OtcRule`, `PrescriptionRule`, the customer discounts (especially quantity above 1) or the rejection log.
- **`getRejections()` exposes a private record type.** It works, but outside code cannot name the type.

## Repository notes

Two commit entries appear with similar messages ("docs: correct Money normalisation comment and remove redundant strip call" and "docs: Correct prescriber comment on Transaction") because an initial commit was followed by a correction commit in each case. They are not duplicate changes: the second commit in each pair corrects the first. The commit "refactor: rename variables for consistency and clarity" renames `subtotal` and `total` inside `Pharmacy.dispense` to reflect that both are now `Money` objects rather than primitives, making the type explicit at the variable name level.

## Commit history

Commits are small and use the `feat` / `fix` / `refactor` / `test` / `docs` prefixes. The first commits are the untouched original, the baseline output, the diagnosis and the golden test, so the "before" is preserved. `git log --oneline` shows one design decision per commit.