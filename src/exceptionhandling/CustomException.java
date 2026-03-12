package exceptionhandling;

/*
 * =============================================================================
 * CustomException.java — Creating Custom Exception Classes
 * =============================================================================
 *
 * CONCEPT: Custom Exceptions
 * ---------------------------
 * You can create your own exception classes by extending:
 *   Exception          → creates a CHECKED custom exception
 *   RuntimeException   → creates an UNCHECKED custom exception
 *
 * WHEN TO CREATE CUSTOM EXCEPTIONS:
 *  - When standard exceptions don't describe your specific error well
 *  - When you want to add custom fields (error codes, etc.)
 *  - When you want to group related exceptions in your domain
 *
 * CONVENTION:
 *  - Exception class names end with "Exception"
 *  - Provide multiple constructors (default, message, message+cause)
 *  - Include serialVersionUID for Serializable (Exceptions are Serializable)
 *
 * HOW TO RUN:
 *  $ javac -d out src/exceptionhandling/CustomException.java
 *  $ java -cp out exceptionhandling.CustomException
 * =============================================================================
 */

// ── CUSTOM CHECKED EXCEPTION (extends Exception) ─────────────────────────────
// Callers MUST handle or declare this exception
class InsufficientFundsException extends Exception {
    private static final long serialVersionUID = 1L;

    private double amount;       // custom field: how much was short
    private double balance;      // custom field: what the balance was

    // Constructor 1: no-args
    InsufficientFundsException() {
        super("Insufficient funds");
    }

    // Constructor 2: just a message
    InsufficientFundsException(String message) {
        super(message);
    }

    // Constructor 3: with custom fields
    InsufficientFundsException(double amount, double balance) {
        super(String.format("Insufficient funds: tried to withdraw $%.2f but balance is $%.2f", amount, balance));
        this.amount = amount;
        this.balance = balance;
    }

    // Constructor 4: message + cause (for exception chaining)
    InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters for custom fields
    public double getAmount()  { return amount; }
    public double getBalance() { return balance; }
}

// ── CUSTOM UNCHECKED EXCEPTION (extends RuntimeException) ────────────────────
// Callers do NOT have to handle this — optional
class InvalidAgeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int age;
    private int errorCode;

    InvalidAgeException(int age) {
        super("Invalid age: " + age + ". Must be between 0 and 150.");
        this.age = age;
        this.errorCode = 400;   // like an HTTP error code
    }

    InvalidAgeException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getAge()       { return age; }
    public int getErrorCode() { return errorCode; }
}

// ── EXCEPTION HIERARCHY: base domain exception + specific subclasses ──────────
class AppException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int errorCode;

    AppException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() { return errorCode; }
}

class ValidationException extends AppException {
    private static final long serialVersionUID = 1L;
    ValidationException(String field) {
        super("Validation failed for field: " + field, 422);
    }
}

class NotFoundException extends AppException {
    private static final long serialVersionUID = 1L;
    NotFoundException(String resource) {
        super(resource + " not found", 404);
    }
}

// ── USAGE EXAMPLE ─────────────────────────────────────────────────────────────
class SavingsAccount {
    private double balance;
    private String owner;

    SavingsAccount(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = initialBalance;
    }

    void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        balance += amount;
    }

    void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (amount > balance) {
            throw new InsufficientFundsException(amount, balance);  // custom exception!
        }
        balance -= amount;
        System.out.printf("[%s] Withdrew $%.2f. Balance: $%.2f%n", owner, amount, balance);
    }

    double getBalance() { return balance; }
}

public class CustomException {

    public static void main(String[] args) {

        // ── CUSTOM CHECKED EXCEPTION ──────────────────────────────────────────
        System.out.println("=== Custom Checked Exception: InsufficientFundsException ===");

        SavingsAccount account = new SavingsAccount("Alice", 1000.0);
        double[] amounts = {200.0, 500.0, 600.0};

        for (double amount : amounts) {
            try {
                account.withdraw(amount);
            } catch (InsufficientFundsException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.printf("  Tried to withdraw: $%.2f%n", e.getAmount());
                System.out.printf("  Available balance: $%.2f%n", e.getBalance());
            }
        }

        // ── CUSTOM UNCHECKED EXCEPTION ────────────────────────────────────────
        System.out.println("\n=== Custom Unchecked Exception: InvalidAgeException ===");

        int[] ages = {25, -1, 200, 0};
        for (int age : ages) {
            try {
                validateAge(age);
            } catch (InvalidAgeException e) {
                System.out.println("Error [code " + e.getErrorCode() + "]: " + e.getMessage());
            }
        }

        // ── EXCEPTION HIERARCHY ───────────────────────────────────────────────
        System.out.println("\n=== Exception Hierarchy ===");

        AppException[] exceptions = {
            new ValidationException("email"),
            new NotFoundException("User"),
            new AppException("Generic app error", 500)
        };

        for (AppException e : exceptions) {
            System.out.println("[" + e.getErrorCode() + "] " + e.getMessage());
        }

        // Catch by parent type:
        try {
            throw new NotFoundException("Product");
        } catch (AppException e) {
            System.out.println("Caught as AppException: " + e.getMessage() + " (code " + e.getErrorCode() + ")");
        }
    }

    static void validateAge(int age) {
        if (age < 0 || age > 150) {
            throw new InvalidAgeException(age);   // throw custom unchecked exception
        }
        System.out.println("Age " + age + " is valid.");
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Custom Checked Exception: InsufficientFundsException ===
 * [Alice] Withdrew $200.00. Balance: $800.00
 * [Alice] Withdrew $500.00. Balance: $300.00
 * Error: Insufficient funds: tried to withdraw $600.00 but balance is $300.00
 *   Tried to withdraw: $600.00
 *   Available balance: $300.00
 *
 * === Custom Unchecked Exception: InvalidAgeException ===
 * Age 25 is valid.
 * Error [code 400]: Invalid age: -1. Must be between 0 and 150.
 * Error [code 400]: Invalid age: 200. Must be between 0 and 150.
 * Age 0 is valid.
 *
 * === Exception Hierarchy ===
 * [422] Validation failed for field: email
 * [404] User not found
 * [500] Generic app error
 * Caught as AppException: Product not found (code 404)
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Not including serialVersionUID → serialization warnings
 * 2. Only providing one constructor — provide at least (String msg) and (String msg, Throwable cause)
 * 3. Extending Exception for everything → force callers to handle even minor issues
 *    Use RuntimeException for programming errors; Exception for recoverable errors.
 * 4. Swallowing the cause when wrapping exceptions:
 *    throw new AppException("failed"); ← original cause lost!
 *    throw new AppException("failed", originalException); ← correct — preserves cause chain
 */
