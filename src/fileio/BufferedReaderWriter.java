package fileio;

import java.io.*;
import java.nio.file.*;

/*
 * =============================================================================
 * BufferedReaderWriter.java — Efficient I/O with Buffered Streams
 * =============================================================================
 *
 * CONCEPT: Buffered I/O
 * ----------------------
 * Buffered streams add an in-memory buffer layer:
 *
 *   Without buffer: Every read/write call goes directly to disk
 *                   (OS system call each time — slow!)
 *
 *   With buffer:    Reads/writes happen to memory buffer
 *                   Buffer is flushed to disk in larger chunks
 *                   Dramatically faster for many small operations
 *
 * CLASSES:
 *  BufferedReader  — Wraps a Reader, adds buffering + readLine()
 *  BufferedWriter  — Wraps a Writer, adds buffering + newLine()
 *  BufferedInputStream  — byte-level buffered input
 *  BufferedOutputStream — byte-level buffered output
 *
 * CHAINING (Decorator pattern):
 *  BufferedReader br = new BufferedReader(new FileReader("file.txt"));
 *                                         ↑
 *                              FileReader reads chars from file
 *              ↑
 *          BufferedReader adds buffering layer on top
 *
 * HOW TO RUN:
 *  $ javac -d out src/fileio/BufferedReaderWriter.java
 *  $ java -cp out fileio.BufferedReaderWriter
 * =============================================================================
 */
public class BufferedReaderWriter {

    public static void main(String[] args) {

        String filename = "buffered_demo.txt";

        // ── BUFFEREDWRITER ─────────────────────────────────────────────────────
        System.out.println("=== BufferedWriter ===");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("Line 1: Java BufferedWriter");
            bw.newLine();   // platform-independent newline (\n on Mac/Linux, \r\n on Windows)
            bw.write("Line 2: Efficient file writing");
            bw.newLine();
            bw.write("Line 3: Uses in-memory buffer");
            bw.newLine();
            bw.write("Line 4: Flushes to disk efficiently");
            bw.flush();   // explicit flush (also happens on close)
            System.out.println("Written with BufferedWriter.");
        } catch (IOException e) {
            System.out.println("Write error: " + e.getMessage());
        }

        // ── BUFFEREDREADER ─────────────────────────────────────────────────────
        System.out.println("\n=== BufferedReader ===");

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {   // readLine() returns null at EOF
                count++;
                System.out.println(count + ": " + line);
            }
            System.out.println("Total lines: " + count);
        } catch (IOException e) {
            System.out.println("Read error: " + e.getMessage());
        }

        // ── BUFFERED STREAMS WITH LARGE DATA ──────────────────────────────────
        System.out.println("\n=== Writing large data efficiently ===");

        String largefile = "large_data.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(largefile))) {
            for (int i = 1; i <= 100; i++) {
                bw.write(String.format("Record %d: value=%d, square=%d%n", i, i, i * i));
            }
            System.out.println("Wrote 100 records to " + largefile);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Read back and count lines
        try (BufferedReader br = new BufferedReader(new FileReader(largefile))) {
            long lines = br.lines().count();   // Java 8+ Stream API on BufferedReader
            System.out.println("Lines in large file: " + lines);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ── BUFFEREDREADER LINES() STREAM ─────────────────────────────────────
        System.out.println("\n=== BufferedReader.lines() Stream (Java 8+) ===");

        try (BufferedReader br = new BufferedReader(new FileReader(largefile))) {
            br.lines()
              .filter(l -> l.contains("square=25") || l.contains("square=100"))
              .forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // ── NIO FILES STREAMS (modern approach) ───────────────────────────────
        System.out.println("\n=== NIO Files.newBufferedReader/Writer (Java 8+) ===");

        Path nioPath = Paths.get("nio_demo.txt");
        try (BufferedWriter bw = Files.newBufferedWriter(nioPath)) {
            bw.write("NIO Path-based writing");
            bw.newLine();
            bw.write("Cleaner API than java.io");
        } catch (IOException e) {
            System.out.println("Write error: " + e.getMessage());
        }

        try (BufferedReader br = Files.newBufferedReader(nioPath)) {
            br.lines().forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Read error: " + e.getMessage());
        }

        // ── CLEANUP ───────────────────────────────────────────────────────────
        for (String f : new String[]{filename, largefile, "nio_demo.txt"}) {
            new File(f).delete();
        }
        System.out.println("\nCleaned up all demo files.");
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === BufferedWriter ===
 * Written with BufferedWriter.
 *
 * === BufferedReader ===
 * 1: Line 1: Java BufferedWriter
 * 2: Line 2: Efficient file writing
 * 3: Line 3: Uses in-memory buffer
 * 4: Line 4: Flushes to disk efficiently
 * Total lines: 4
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Not flushing BufferedWriter: data may remain in the buffer and not be written to disk.
 *    try-with-resources automatically calls close() which calls flush().
 *    If you can't use try-with-resources, call bw.flush() explicitly.
 *
 * 2. Using FileWriter.write(int) with a char value for line numbers:
 *    fw.write(65) writes char 'A', not the string "65"! Use fw.write("65").
 *
 * 3. Default buffer size (8192 chars) is usually fine.
 *    For very large files with frequent reads, a larger buffer (new BufferedReader(fr, 65536))
 *    can help.
 *
 * 4. BufferedReader.readLine() strips the newline character.
 *    If you need to preserve the newline, use read() instead.
 *
 * 5. Not using bw.newLine() — using "\n" is not portable:
 *    "\n" works on Mac/Linux but Windows needs "\r\n".
 *    bw.newLine() uses the correct platform line separator.
 */
