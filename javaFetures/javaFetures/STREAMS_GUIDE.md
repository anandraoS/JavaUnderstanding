# Streams Package - Complete Interview Guide (12+ Years)

This module explains Java Streams end-to-end with runnable examples.

## Files
- `src/streams/StreamBasics.java`
- `src/streams/StreamCollectors.java`
- `src/streams/StreamAdvanced.java`
- `src/streams/StreamPitfalls.java`
- `src/streams/StreamsRunner.java`

## Topics Covered
- Stream creation, laziness, pipeline stages
- map vs flatMap
- reduce and parallel reduction
- primitive streams and statistics
- collectors: groupingBy, partitioningBy, toMap, joining
- parallel streams: ordering, forEachOrdered
- spliterator basics
- custom collectors
- stream pitfalls: reuse, side effects, ordering

## Run
```bash
cd src
javac streams\*.java
java streams.StreamsRunner
```

## Interview Focus
- Explain lazy evaluation and why streams are single-use
- Correct reduction in parallel streams (identity + combiner)
- collector selection and downstream collectors
- side effects and ordering hazards
- when to prefer parallel streams

