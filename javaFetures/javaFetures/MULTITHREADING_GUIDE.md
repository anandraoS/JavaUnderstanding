# Multithreading Package - Complete Interview Guide (12+ Years)

This module provides a practical and interview-focused walkthrough of Java concurrency.

## Files
- `src/multithreading/ThreadBasics.java`
- `src/multithreading/SynchronizationExamples.java`
- `src/multithreading/ExecutorExamples.java`
- `src/multithreading/ConcurrencyUtilitiesExamples.java`
- `src/multithreading/ConcurrentCollectionsExamples.java`
- `src/multithreading/ThreadingPitfalls.java`
- `src/multithreading/ThreadingRunner.java`

## Topics Covered
- thread lifecycle, interrupt, join, naming
- synchronization, locks, visibility (volatile)
- atomic operations
- ExecutorService, Future, CompletableFuture
- ScheduledExecutorService
- CountDownLatch, CyclicBarrier, Semaphore, Phaser
- concurrent collections and BlockingQueue
- deadlock avoidance patterns
- ThreadLocal usage

## Run
```bash
cd src
javac multithreading\*.java
java multithreading.ThreadingRunner
```

## Interview Focus
- visibility vs atomicity
- race conditions and correct fixes
- deadlock detection and avoidance
- executor sizing and shutdown
- when to use concurrency utilities

