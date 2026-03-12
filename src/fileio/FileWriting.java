package fileio;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/*
 * =============================================================================
 * FileWriting.java — Writing Files in Java
 * =============================================================================
 *
 * CONCEPT: File Writing
 * ----------------------
 * Java provides multiple ways to write files:
 *
 *  1. FileWriter        — writes characters, can append or overwrite
 *  2. PrintWriter       — writes formatted text (like println, printf)
 *  3. Files.writeString()  — Java 11+, writes a String to a file (simple!)
 *  4. Files.write()     — writes bytes or lines to a file
 *
 * APPEND vs OVERWRITE:
 *  new FileWriter(file, true)   → APPEND mode (adds to existing content)
 *  new FileWriter(file, false)  → OVERWRITE mode (clears existing content)
 *  new FileWriter(file)         → OVERWRITE by default
 *
 * HOW TO RUN:
 *  $ javac -d out src/fileio/FileWriting.java
 *  $ java -cp out fileio.FileWriting
 * =============================================================================
 */
public class FileWriting {

    public static void main(String[] args) {

        // ── METHOD 1: FileWriter ───────────────────────────────────────────────
        System.out.println("=== FileWriter (overwrite mode) ===");
        String filename1 = "output1.txt";

        try (FileWriter fw = new FileWriter(filename1)) {   // overwrite mode
            fw.write("Hello, FileWriter!\n");
            fw.write("This is the second line.\n");
            fw.write("Written using FileWriter.");
            System.out.println("Written to " + filename1);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ── METHOD 1B: FileWriter in APPEND mode ──────────────────────────────
        System.out.println("\n=== FileWriter (append mode) ===");
        try (FileWriter fw = new FileWriter(filename1, true)) {   // append = true
            fw.write("\n--- APPENDED CONTENT ---\n");
            fw.write("This was added later.");
            System.out.println("Appended to " + filename1);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Read and display what was written
        readAndPrint(filename1);

        // ── METHOD 2: PrintWriter (formatted output) ───────────────────────────
        System.out.println("\n=== PrintWriter (formatted output) ===");
        String filename2 = "output2.txt";

        try (PrintWriter pw = new PrintWriter(filename2)) {
            pw.println("=== Student Report ===");      // println adds newline
            pw.printf("%-15s %5s %6s%n", "Name", "Age", "GPA");
            pw.println("-".repeat(27));

            String[][] students = {{"Alice", "20", "3.85"}, {"Bob", "22", "3.60"}, {"Charlie", "21", "3.92"}};
            for (String[] s : students) {
                pw.printf("%-15s %5s %6s%n", s[0], s[1], s[2]);
            }

            System.out.println("Report written to " + filename2);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        readAndPrint(filename2);

        // ── METHOD 3: Files.writeString (Java 11+) ────────────────────────────
        System.out.println("\n=== Files.writeString() (Java 11+) ===");
        String filename3 = "output3.txt";

        try {
            String content = "Java 11 makes file writing simple!\nJust one line of code!";
            Files.writeString(Paths.get(filename3), content);
            System.out.println("Written to " + filename3);

            // APPEND with StandardOpenOption
            Files.writeString(Paths.get(filename3), "\n(Appended with NIO)",
                    StandardOpenOption.APPEND);
            System.out.println("Appended to " + filename3);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        readAndPrint(filename3);

        // ── METHOD 4: Files.write (with lines list) ────────────────────────────
        System.out.println("\n=== Files.write() (list of lines) ===");
        String filename4 = "output4.txt";

        java.util.List<String> lines = java.util.List.of(
            "Line 1: First entry",
            "Line 2: Second entry",
            "Line 3: Third entry"
        );

        try {
            Files.write(Paths.get(filename4), lines);
            System.out.println("Written to " + filename4);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        readAndPrint(filename4);

        // ── CLEANUP ───────────────────────────────────────────────────────────
        for (String f : new String[]{filename1, filename2, filename3, filename4}) {
            new java.io.File(f).delete();
        }
        System.out.println("\nCleaned up all output files.");
    }

    // Helper method to read and display file content
    static void readAndPrint(String filename) {
        System.out.println("-- Content of " + filename + " --");
        try {
            String content = Files.readString(Paths.get(filename));
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("Cannot read: " + e.getMessage());
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === FileWriter (overwrite mode) ===
 * Written to output1.txt
 *
 * === FileWriter (append mode) ===
 * Appended to output1.txt
 * -- Content of output1.txt --
 * Hello, FileWriter!
 * This is the second line.
 * Written using FileWriter.
 * --- APPENDED CONTENT ---
 * This was added later.
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Forgetting to close FileWriter — data may not be flushed to disk!
 *    Use try-with-resources. FileWriter has an internal buffer.
 *
 * 2. new FileWriter(file) without flush/close → last bytes may not be written
 *
 * 3. PrintWriter doesn't throw IOException from print/println!
 *    Use checkError() to see if there were write errors.
 *
 * 4. File permissions on Mac: if you can't write to a file, check permissions
 *    ls -la filename  (in terminal)
 *    chmod 644 filename
 *
 * 5. Relative paths are relative to the working directory (where you run java).
 *    Run from the project root: java -cp out fileio.FileWriting
 */
