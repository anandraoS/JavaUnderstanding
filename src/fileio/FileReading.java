package fileio;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/*
 * =============================================================================
 * FileReading.java — Reading Files in Java
 * =============================================================================
 *
 * CONCEPT: File Reading
 * ----------------------
 * Java provides multiple ways to read files:
 *
 *  1. FileReader          — reads characters one by one (low-level)
 *  2. BufferedReader      — reads line by line (efficient, recommended)
 *  3. Scanner             — reads tokens, convenient for formatted data
 *  4. Files.readString()  — Java 11+, reads entire file as String (simple!)
 *  5. Files.readAllLines()— Java 8+, reads all lines into a List<String>
 *
 * CHECKED EXCEPTIONS:
 *  File I/O throws IOException (checked) — must handle with try-catch or throws
 *
 * ALWAYS CLOSE RESOURCES:
 *  Use try-with-resources to automatically close file handles.
 *
 * NOTE: This demo creates a sample file first, then reads it.
 *
 * HOW TO RUN:
 *  $ javac -d out src/fileio/FileReading.java
 *  $ java -cp out fileio.FileReading
 * =============================================================================
 */
public class FileReading {

    public static void main(String[] args) {

        // First, create a sample file to read
        String filename = "sample_read.txt";
        createSampleFile(filename);

        // ── METHOD 1: FileReader (character by character) ──────────────────────
        System.out.println("=== FileReader (char by char) ===");
        try (FileReader reader = new FileReader(filename)) {
            int ch;
            StringBuilder content = new StringBuilder();
            while ((ch = reader.read()) != -1) {   // read() returns -1 at end of file
                content.append((char) ch);
            }
            System.out.println("Content:\n" + content);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // ── METHOD 2: BufferedReader (line by line) ────────────────────────────
        System.out.println("=== BufferedReader (line by line) ===");
        try (java.io.BufferedReader br = new java.io.BufferedReader(new FileReader(filename))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {   // readLine() returns null at EOF
                System.out.println("Line " + lineNum++ + ": " + line);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ── METHOD 3: Files.readAllLines (Java 8+) ────────────────────────────
        System.out.println("\n=== Files.readAllLines() (Java 8+) ===");
        java.nio.file.Path path = java.nio.file.Paths.get(filename);
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
            System.out.println("Total lines: " + lines.size());
            lines.forEach(l -> System.out.println("  " + l));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ── METHOD 4: Files.readString (Java 11+) ─────────────────────────────
        System.out.println("\n=== Files.readString() (Java 11+) ===");
        try {
            String fullContent = java.nio.file.Files.readString(path);
            System.out.println("Full file content (" + fullContent.length() + " chars):");
            System.out.println(fullContent);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ── FILE METADATA ─────────────────────────────────────────────────────
        System.out.println("=== File Metadata ===");
        File file = new File(filename);
        System.out.println("Name:     " + file.getName());
        System.out.println("Path:     " + file.getAbsolutePath());
        System.out.println("Exists:   " + file.exists());
        System.out.println("Size:     " + file.length() + " bytes");
        System.out.println("Readable: " + file.canRead());

        // ── CHECKING IF FILE EXISTS ────────────────────────────────────────────
        System.out.println("\n=== Checking file existence ===");
        File missing = new File("nonexistent_file.txt");
        if (!missing.exists()) {
            System.out.println("File does not exist: " + missing.getName());
        }

        // Cleanup
        file.delete();
        System.out.println("\nCleaned up " + filename);
    }

    // Helper: create a sample file for reading demos
    static void createSampleFile(String filename) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(filename)) {
            pw.println("Hello, Java File I/O!");
            pw.println("This is line 2.");
            pw.println("Reading files is important.");
            pw.println("Java makes it easy.");
        } catch (IOException e) {
            System.out.println("Failed to create sample file: " + e.getMessage());
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === FileReader (char by char) ===
 * Content:
 * Hello, Java File I/O!
 * This is line 2.
 * Reading files is important.
 * Java makes it easy.
 *
 * === BufferedReader (line by line) ===
 * Line 1: Hello, Java File I/O!
 * Line 2: This is line 2.
 * Line 3: Reading files is important.
 * Line 4: Java makes it easy.
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Not closing the file → resource leak (file handles remain open)
 *    Always use try-with-resources for automatic closing.
 *
 * 2. FileNotFoundException: the file path is wrong or file doesn't exist
 *    Run from project root when using relative paths.
 *
 * 3. Not handling IOException (checked exception) → compile error
 *
 * 4. Reading char by char with FileReader is slow for large files
 *    Use BufferedReader for performance.
 *
 * 5. File.delete() on Mac: ensure the file isn't locked by another process.
 */
