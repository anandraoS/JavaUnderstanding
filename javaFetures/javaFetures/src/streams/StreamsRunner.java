package streams;

/**
 * Simple runner to execute all stream examples in order.
 */
public class StreamsRunner {
    public static void main(String[] args) {
        System.out.println("STREAMS MODULE - COMPLETE WALKTHROUGH\n");

        StreamBasics.main(new String[]{});
        StreamCollectors.main(new String[]{});
        StreamAdvanced.main(new String[]{});
        StreamPitfalls.main(new String[]{});

        System.out.println("\nSTREAMS MODULE COMPLETED");
    }
}

