import collections.*;
import streams.*;
import multithreading.*;
import java.util.Scanner;

/**
 * Java Collections Framework - Comprehensive Tutorial
 * Main menu to run all collection examples for experienced developers
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print("\nEnter your choice (0-9): ");

            try {
                int choice = scanner.nextInt();

                if (choice == 0) {
                    System.out.println("\n👋 Thank you for learning Java Collections Framework!");
                    System.out.println("Keep coding! 🚀");
                    break;
                }

                System.out.println("\n" + "=".repeat(80));
                executeChoice(choice);
                System.out.println("=".repeat(80));

                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Consume newline
                scanner.nextLine(); // Wait for Enter

            } catch (Exception e) {
                System.err.println("\n❌ Error: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("        JAVA COLLECTIONS FRAMEWORK - COMPREHENSIVE TUTORIAL");
        System.out.println("        For Experienced Developers (10+ Years)");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("📚 SELECT A TOPIC:");
        System.out.println();
        System.out.println("  1. 📋 List Examples");
        System.out.println("     - ArrayList vs LinkedList performance");
        System.out.println("     - CopyOnWriteArrayList, Vector, fail-fast/fail-safe iterators");
        System.out.println();
        System.out.println("  2. 🔷 Set Examples");
        System.out.println("     - HashSet internals, hash collision handling");
        System.out.println("     - TreeSet, NavigableSet, EnumSet, concurrent sets");
        System.out.println();
        System.out.println("  3. 🗺️  Map Examples");
        System.out.println("     - HashMap internals, Java 8 treeification");
        System.out.println("     - LRU Cache, WeakHashMap, ConcurrentHashMap, compute operations");
        System.out.println();
        System.out.println("  4. 📥 Queue Examples");
        System.out.println("     - PriorityQueue, ArrayDeque, BlockingQueue");
        System.out.println("     - Producer-Consumer pattern, DelayQueue, LinkedTransferQueue");
        System.out.println();
        System.out.println("  5. 🛠️  Collection Utilities");
        System.out.println("     - Immutable collections, synchronized wrappers");
        System.out.println("     - Arrays utilities, empty collections, collection factories");
        System.out.println();
        System.out.println("  6. 🎓 Advanced Concepts");
        System.out.println("     - Capacity planning, collection views, custom implementations");
        System.out.println("     - Performance optimization, comparators, common pitfalls");
        System.out.println();
        System.out.println("  7. 🚀 Run ALL Examples");
        System.out.println("     - Execute all tutorials in sequence (recommended for review)");
        System.out.println();
        System.out.println("  8. 🌊 Streams (Complete Guide)");
        System.out.println("     - Stream basics, collectors, advanced topics, pitfalls");
        System.out.println();
        System.out.println("  9. 🧵 Multithreading (Complete Guide)");
        System.out.println("     - Threads, synchronization, executors, utilities, pitfalls");
        System.out.println();
        System.out.println("  0. 🚪 Exit");
        System.out.println();
        System.out.println("=".repeat(80));
    }

    private static void executeChoice(int choice) throws InterruptedException {
        switch (choice) {
            case 1:
                System.out.println("📋 LIST EXAMPLES - Deep Dive into List Implementations\n");
                ListExamples.main(new String[]{});
                break;

            case 2:
                System.out.println("🔷 SET EXAMPLES - Mastering Set Interface\n");
                SetExamples.main(new String[]{});
                break;

            case 3:
                System.out.println("🗺️  MAP EXAMPLES - Expert Level Map Operations\n");
                MapExamples.main(new String[]{});
                break;

            case 4:
                System.out.println("📥 QUEUE EXAMPLES - Queue and Deque Interfaces\n");
                QueueExamples.main(new String[]{});
                break;

            case 5:
                System.out.println("🛠️  COLLECTION UTILITIES - Framework Utilities and Best Practices\n");
                CollectionUtilities.main(new String[]{});
                break;

            case 6:
                System.out.println("🎓 ADVANCED CONCEPTS - Performance and Optimization\n");
                AdvancedCollectionConcepts.main(new String[]{});
                break;

            case 7:
                System.out.println("🚀 RUNNING ALL EXAMPLES\n");
                System.out.println("This will execute all tutorials in sequence...\n");

                System.out.println("\n" + "▶".repeat(40) + " LIST EXAMPLES " + "▶".repeat(40));
                ListExamples.main(new String[]{});

                System.out.println("\n" + "▶".repeat(40) + " SET EXAMPLES " + "▶".repeat(40));
                SetExamples.main(new String[]{});

                System.out.println("\n" + "▶".repeat(40) + " MAP EXAMPLES " + "▶".repeat(40));
                MapExamples.main(new String[]{});

                System.out.println("\n" + "▶".repeat(40) + " QUEUE EXAMPLES " + "▶".repeat(40));
                QueueExamples.main(new String[]{});

                System.out.println("\n" + "▶".repeat(40) + " COLLECTION UTILITIES " + "▶".repeat(40));
                CollectionUtilities.main(new String[]{});

                System.out.println("\n" + "▶".repeat(40) + " ADVANCED CONCEPTS " + "▶".repeat(40));
                AdvancedCollectionConcepts.main(new String[]{});

                System.out.println("\n" + "▶".repeat(40) + " STREAMS " + "▶".repeat(40));
                StreamsRunner.main(new String[]{});

                System.out.println("\n" + "▶".repeat(40) + " MULTITHREADING " + "▶".repeat(40));
                ThreadingRunner.main(new String[]{});

                System.out.println("\n✅ ALL EXAMPLES COMPLETED!");
                break;

            case 8:
                System.out.println("🌊 STREAMS - COMPLETE GUIDE\n");
                StreamsRunner.main(new String[]{});
                break;

            case 9:
                System.out.println("🧵 MULTITHREADING - COMPLETE GUIDE\n");
                ThreadingRunner.main(new String[]{});
                break;

            default:
                System.out.println("❌ Invalid choice. Please select 0-9.");
        }
    }
}