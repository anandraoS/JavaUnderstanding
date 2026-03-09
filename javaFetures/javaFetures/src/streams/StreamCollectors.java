package streams;

import java.util.*;
import java.util.stream.*;

/**
 * Collector patterns expected in senior interviews.
 * Focus: groupingBy, partitioningBy, mapping, summarizing, joining.
 */
public class StreamCollectors {

    private static class Employee {
        private final String name;
        private final String dept;
        private final int salary;

        Employee(String name, String dept, int salary) {
            this.name = name;
            this.dept = dept;
            this.salary = salary;
        }

        public String getName() { return name; }
        public String getDept() { return dept; }
        public int getSalary() { return salary; }

        @Override
        public String toString() {
            return name + "(" + dept + "," + salary + ")";
        }
    }

    /**
     * Question: How do groupingBy and partitioningBy differ?
     */
    public static void demonstrateGroupingAndPartitioning() {
        System.out.println("=== groupingBy vs partitioningBy ===");

        List<Employee> employees = Arrays.asList(
                new Employee("Ann", "ENG", 120),
                new Employee("Ben", "ENG", 90),
                new Employee("Cara", "HR", 80),
                new Employee("Dan", "HR", 95)
        );

        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept));

        Map<Boolean, List<Employee>> highPaid = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() >= 100));

        System.out.println("By dept: " + byDept);
        System.out.println("High paid partition: " + highPaid);
    }

    /**
     * Question: How to do downstream collectors?
     */
    public static void demonstrateDownstreamCollectors() {
        System.out.println("\n=== Downstream Collectors ===");

        List<Employee> employees = Arrays.asList(
                new Employee("Ann", "ENG", 120),
                new Employee("Ben", "ENG", 90),
                new Employee("Cara", "HR", 80),
                new Employee("Dan", "HR", 95)
        );

        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept, Collectors.counting()));

        Map<String, IntSummaryStatistics> salaryStats = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept,
                        Collectors.summarizingInt(Employee::getSalary)));

        Map<String, List<String>> namesByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept,
                        Collectors.mapping(Employee::getName, Collectors.toList())));

        System.out.println("Count by dept: " + countByDept);
        System.out.println("Salary stats: " + salaryStats);
        System.out.println("Names by dept: " + namesByDept);
    }

    /**
     * Question: How to collect to Map safely?
     */
    public static void demonstrateToMap() {
        System.out.println("\n=== Collectors.toMap ===");

        List<Employee> employees = Arrays.asList(
                new Employee("Ann", "ENG", 120),
                new Employee("Ann", "ENG", 125),
                new Employee("Ben", "ENG", 90)
        );

        Map<String, Integer> maxSalaryByName = employees.stream()
                .collect(Collectors.toMap(
                        Employee::getName,
                        Employee::getSalary,
                        Integer::max
                ));

        System.out.println("Max salary by name: " + maxSalaryByName);
    }

    /**
     * Question: Show joining and reducing collectors.
     */
    public static void demonstrateJoiningAndReducing() {
        System.out.println("\n=== Joining and Reducing ===");

        List<String> tags = Arrays.asList("streams", "java", "interview");
        String joined = tags.stream().collect(Collectors.joining(", ", "[", "]"));

        int total = Arrays.asList(1, 2, 3, 4)
                .stream()
                .collect(Collectors.reducing(0, Integer::intValue, Integer::sum));

        System.out.println("Joined: " + joined);
        System.out.println("Reduced total: " + total);
    }

    public static void main(String[] args) {
        demonstrateGroupingAndPartitioning();
        demonstrateDownstreamCollectors();
        demonstrateToMap();
        demonstrateJoiningAndReducing();
    }
}

