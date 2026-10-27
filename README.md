# DAA Assignment 3 — Graphs

**Kuralbaev Erasyl — SE2516**

This beginner-readable Java 17 project implements adjacency-list graphs, DSU with union-by-size and path compression, Kruskal, Prim, iterative Kosaraju, Kahn and DAG shortest paths.

## Running

```bash
mvn test
mvn exec:java -Dexec.args=benchmark
python3 scripts/plots.py
```

## Current verification

The latest separately downloadable ZIP contains all **50 benchmark rows**, three PNG plots, corrected tests and an extended report. Local compilation and all seven test method bodies passed in a Java assertion harness; the official Maven/JUnit runner could not be invoked. The random generator results **do not match all reference numbers in the handout**, so grading compliance is not yet confirmed. The ZIP remains the more complete version than this GitHub tree; tag v1.0 has not been created. AI assistance was used for code and documentation; the student should review all work.
