# DAA Assignment 3 — Graphs

**Student:** Kuralbaev Erasyl  
**Group:** SE2516

## How to run (Java 17+, Maven)

```bash
mvn test
mvn exec:java -Dexec.args=benchmark
```

Algorithms in `src/main/java/daa/Main.java`: Graph, DSU, Kruskal, Prim, Kosaraju, Kahn, DAG shortest paths. The code is formatted to be easier for a beginner to read.

## What each algorithm does

- **DSU:** joins connected groups.
- **Kruskal:** picks low-cost edges without making cycles.
- **Prim:** grows a low-cost tree using a priority queue.
- **Kosaraju:** groups vertices that can reach one another.
- **Kahn:** sorts jobs by dependencies and detects cycles.
- **DAG shortest paths:** finds lowest-cost paths with a topological order.

## Submission status

This is a **work in progress**, not a verified final submission. The separate downloadable beginner ZIP has sample data, charts, CSV and a longer report. Its CSV has 45/50 measured rows; the large dense case is missing. Some reference figures still disagree with the handout. Maven/JUnit has not been executed here. Git tag v1.0 was not created. Do not claim full grading compliance until these issues are resolved.

## AI assistance

AI was used to draft and revise source code, tests and documentation. Review the project and be able to explain it yourself.
