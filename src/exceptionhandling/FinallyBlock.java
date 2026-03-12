package exceptionhandling;

/*
 * =============================================================================
 * FinallyBlock.java — finally Block Behavior and Resource Cleanup
 * =============================================================================
 *
 * CONCEPT: finally Block
 * -----------------------
 * The 'finally' block ALWAYS executes, regardless of whether:
 *  - The try block completes normally
 *  - An exception is thrown (and caught or not caught)
 *  - A return statement is executed in try/catch
 *
 * WHEN finally does NOT run:
 *  - If System.exit() is called
 *  - If the JVM crashes (OutOfMemoryError, etc.)
 *  - If the thread running the code is killed
 *
 * PRIMARY USE CASES:
 *  - Releasing resources: closing files, database connections, sockets
 *  - Clean-up code that must always run
 *  - Java 7+: try-with-resources is preferred for AutoCloseable resources
 *
 * HOW TO RUN:
 *  $ javac -d out src/exceptionhandling/FinallyBlock.java
 *  $ java -cp out exceptionhandling.FinallyBlock
 * =============================================================================
 */
public class FinallyBlock {

    public static void main(String[] args) {

        // ── FINALLY ALWAYS RUNS ────────────────────────────────────────────────
        System.out.println("=== Finally always runs ===");

        System.out.println("--- Case 1: No exception ---");
        try {
            System.out.println("Try: no exception");
        } catch (Exception e) {
            System.out.println("Catch: " + e.getMessage());
        } finally {
            System.out.println("Finally: runs even without exception");
        }

        System.out.println("\n--- Case 2: Exception caught ---");
        try {
            System.out.println("Try: about to throw");
            throw new RuntimeException("Test exception");
        } catch (RuntimeException e) {
            System.out.println("Catch: " + e.getMessage());
        } finally {
            System.out.println("Finally: runs after exception is caught");
        }

        System.out.println("\n--- Case 3: Exception NOT caught ---");
        try {
            handleUncaught();
        } catch (RuntimeException e) {
            System.out.println("Outer catch: " + e.getMessage());
        }

        // ── FINALLY WITH RETURN ────────────────────────────────────────────────
        System.out.println("\n=== Finally with return ===");
        int result = methodWithReturn(true);
        System.out.println("Result (exception path): " + result);   // 2 (from finally!)

        result = methodWithReturn(false);
        System.out.println("Result (normal path):    " + result);   // 2 (from finally!)

        // ── RESOURCE CLEANUP PATTERN ──────────────────────────────────────────
        System.out.println("\n=== Resource cleanup in finally ===");

        // Pre-Java 7 style: manual cleanup in finally
        FakeConnection conn = null;
        try {
            conn = new FakeConnection("database_url");
            conn.query("SELECT * FROM users");
            // If an exception occurs here, finally still closes the connection
            conn.query("INVALID_QUERY");
        } catch (Exception e) {
            System.out.println("Query error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.close();   // always close the connection
            }
        }

        // ── TRY-WITH-RESOURCES (preferred Java 7+) ────────────────────────────
        System.out.println("\n=== Try-with-resources (modern approach) ===");
        // AutoCloseable resources are closed automatically
        try (FakeConnection conn2 = new FakeConnection("database_url_2")) {
            conn2.query("SELECT * FROM products");
            // close() is called automatically at end of block
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Connection was automatically closed.");

        // ── MULTIPLE RESOURCES ────────────────────────────────────────────────
        System.out.println("\n=== Multiple resources in try-with ===");
        try (FakeConnection c1 = new FakeConnection("db1");
             FakeConnection c2 = new FakeConnection("db2")) {
            c1.query("SELECT 1");
            c2.query("SELECT 2");
            // Both connections closed in REVERSE order (c2 first, then c1)
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void handleUncaught() {
        try {
            System.out.println("Inner try: throwing exception");
            throw new RuntimeException("Uncaught in inner method");
        } finally {
            System.out.println("Inner finally: runs before exception propagates up");
        }
    }

    static int methodWithReturn(boolean throwException) {
        try {
            if (throwException) {
                throw new RuntimeException("Exception in try");
            }
            return 1;   // would return 1 if no exception, but finally overrides!
        } catch (RuntimeException e) {
            System.out.println("Catch: " + e.getMessage());
            return 2;   // catch returns 2, but finally can override!
        } finally {
            System.out.println("Finally: executing");
            return 2;   // ALWAYS returns 2 — overrides any return in try/catch!
            // NOTE: Having return in finally is generally bad practice!
        }
    }
}

// Simulated database connection for demo
class FakeConnection implements AutoCloseable {
    private String url;
    private boolean closed = false;

    FakeConnection(String url) throws Exception {
        this.url = url;
        System.out.println("Connection opened: " + url);
    }

    void query(String sql) throws Exception {
        if (closed) throw new Exception("Connection is closed");
        if (sql.startsWith("INVALID")) throw new Exception("Invalid SQL: " + sql);
        System.out.println("  Query executed: " + sql);
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            System.out.println("Connection closed: " + url);
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Finally always runs ===
 * --- Case 1: No exception ---
 * Try: no exception
 * Finally: runs even without exception
 *
 * --- Case 2: Exception caught ---
 * Try: about to throw
 * Catch: Test exception
 * Finally: runs after exception is caught
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Having return statements in finally:
 *    This can override the return value from try/catch — very confusing!
 *    Avoid return in finally unless you specifically want to override.
 *
 * 2. Throwing exceptions in finally:
 *    Exceptions thrown in finally REPLACE the original exception!
 *    The original exception is silently lost.
 *
 * 3. Assuming finally doesn't run on exception:
 *    It ALWAYS runs (except System.exit() or JVM crash).
 *
 * 4. Not checking for null before calling close() in finally:
 *    If constructor throws, the variable may be null → NullPointerException in finally!
 *    Always check: if (resource != null) resource.close();
 *
 * 5. Using finally for resource cleanup when try-with-resources is available:
 *    Java 7+ try-with-resources is cleaner and safer — prefer it.
 */
