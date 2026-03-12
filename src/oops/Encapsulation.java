package oops;

/*
 * =============================================================================
 * Encapsulation.java — Data Hiding with Private Fields, Getters, and Setters
 * =============================================================================
 *
 * CONCEPT: Encapsulation
 * -----------------------
 * Encapsulation is one of the four pillars of OOP. It means:
 *  1. Bundling related data (fields) and methods that operate on that data
 *     into a single unit (class)
 *  2. Restricting direct access to some of the class's internal state
 *     (hiding the implementation details)
 *
 * HOW IT'S ACHIEVED IN JAVA:
 *  - Declare fields as PRIVATE (can't be accessed from outside the class)
 *  - Provide PUBLIC getter methods to READ the fields
 *  - Provide PUBLIC setter methods to WRITE the fields (with validation!)
 *
 * BENEFITS:
 *  - Data Integrity: validation logic in setters prevents invalid values
 *  - Flexibility: can change internal implementation without affecting users
 *  - Security: prevents unauthorized access or modification
 *  - Maintainability: centralized validation and logic
 *
 * RECORD (Java 16+): A concise way to create immutable data classes
 *   record Point(int x, int y) {}   → auto-generates constructor, getters, equals, hashCode, toString
 *
 * HOW TO RUN:
 *  $ javac -d out src/oops/Encapsulation.java
 *  $ java -cp out oops.Encapsulation
 * =============================================================================
 */

// Well-encapsulated BankAccount class
class BankAccount {
    // ALL fields are PRIVATE — only accessible through methods
    private String accountNumber;
    private String ownerName;
    private double balance;
    private int pin;

    // Constructor
    BankAccount(String accountNumber, String ownerName, double initialBalance, int pin) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        setBalance(initialBalance);   // use setter for validation
        setPin(pin);
    }

    // ── GETTERS (Accessors) ───────────────────────────────────────────────────
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName()    { return ownerName; }
    public double getBalance()      { return balance; }

    // PIN should never be exposed — no getter for pin!
    // The only way to use PIN is through verifyPin()

    // ── SETTERS (Mutators) with Validation ───────────────────────────────────
    public void setOwnerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Error: Name cannot be empty.");
            return;   // reject invalid value
        }
        this.ownerName = name.trim();
    }

    private void setBalance(double amount) {
        if (amount < 0) {
            System.out.println("Error: Initial balance cannot be negative. Setting to 0.");
            this.balance = 0;
        } else {
            this.balance = amount;
        }
    }

    private void setPin(int pin) {
        if (pin < 1000 || pin > 9999) {   // must be 4-digit
            System.out.println("Warning: PIN must be 4 digits. Using default 0000.");
            this.pin = 0;
        } else {
            this.pin = pin;
        }
    }

    // ── BUSINESS METHODS ─────────────────────────────────────────────────────
    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit Error: Amount must be positive.");
            return false;
        }
        balance += amount;
        System.out.printf("Deposited $%.2f. New balance: $%.2f%n", amount, balance);
        return true;
    }

    public boolean withdraw(double amount, int enteredPin) {
        if (!verifyPin(enteredPin)) {
            System.out.println("Withdrawal Failed: Incorrect PIN.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Withdrawal Error: Amount must be positive.");
            return false;
        }
        if (amount > balance) {
            System.out.printf("Withdrawal Failed: Insufficient funds. Balance: $%.2f%n", balance);
            return false;
        }
        balance -= amount;
        System.out.printf("Withdrawn $%.2f. Remaining balance: $%.2f%n", amount, balance);
        return true;
    }

    private boolean verifyPin(int enteredPin) {
        return this.pin == enteredPin;   // private helper — not accessible outside
    }

    public String getSummary() {
        return String.format("Account: %s | Owner: %s | Balance: $%.2f",
                accountNumber, ownerName, balance);
    }
}

// Another example: immutable encapsulated class
final class ImmutablePoint {
    private final int x;   // final + private = truly immutable
    private final int y;

    ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // Instead of setters, return NEW objects for "modifications"
    public ImmutablePoint translate(int dx, int dy) {
        return new ImmutablePoint(x + dx, y + dy);   // new object!
    }

    @Override
    public String toString() { return "(" + x + ", " + y + ")"; }
}

public class Encapsulation {

    public static void main(String[] args) {

        System.out.println("=== BankAccount Encapsulation Demo ===");
        BankAccount account = new BankAccount("ACC001", "Alice", 5000.0, 1234);

        // Can't access private fields directly:
        // account.balance = 1000000; ← COMPILE ERROR: balance has private access

        // Must use public methods:
        System.out.println("Owner: " + account.getOwnerName());
        System.out.println("Balance: " + account.getBalance());
        System.out.println();

        // Deposits
        account.deposit(1500.0);
        account.deposit(-100.0);   // rejected by validation

        System.out.println();

        // Withdrawals
        account.withdraw(500.0, 1234);   // correct PIN
        account.withdraw(500.0, 9999);   // wrong PIN
        account.withdraw(100000.0, 1234); // insufficient funds

        System.out.println();

        // Setter with validation
        account.setOwnerName("  Bob  ");   // trims whitespace
        System.out.println("Updated owner: " + account.getOwnerName());

        account.setOwnerName("");    // rejected by validation

        System.out.println();
        System.out.println(account.getSummary());

        // ── IMMUTABLE CLASS ───────────────────────────────────────────────────
        System.out.println("\n=== Immutable Class Demo ===");
        ImmutablePoint p1 = new ImmutablePoint(3, 4);
        System.out.println("Original point: " + p1);

        ImmutablePoint p2 = p1.translate(2, -1);  // creates new point, p1 unchanged
        System.out.println("Translated:     " + p2);
        System.out.println("Original still: " + p1);   // unchanged!
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === BankAccount Encapsulation Demo ===
 * Owner: Alice
 * Balance: 5000.0
 *
 * Deposited $1500.00. New balance: $6500.00
 * Deposit Error: Amount must be positive.
 *
 * Withdrawn $500.00. Remaining balance: $6000.00
 * Withdrawal Failed: Incorrect PIN.
 * Withdrawal Failed: Insufficient funds. Balance: $6000.00
 *
 * Updated owner: Bob
 * Error: Name cannot be empty.
 *
 * Account: ACC001 | Owner: Bob | Balance: $6000.00
 *
 * === Immutable Class Demo ===
 * Original point: (3, 4)
 * Translated:     (5, 3)
 * Original still: (3, 4)
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Providing getters AND setters for all private fields defeats encapsulation.
 *    Only expose what's necessary. Some fields should be read-only (getter only).
 *
 * 2. Returning mutable objects from getters breaks encapsulation:
 *    public int[] getData() { return this.data; } ← caller can modify your array!
 *    Fix: return Arrays.copyOf(data, data.length);
 *
 * 3. Setters without validation are just verbose field assignments.
 *    Add validation to make encapsulation meaningful.
 *
 * 4. Making a class 'immutable' but not marking fields as 'final':
 *    Fields can still be changed internally. Use both 'private final' for true immutability.
 */
