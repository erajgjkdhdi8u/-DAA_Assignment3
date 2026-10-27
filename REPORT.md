# DAA Assignment 3 — Graph Algorithms

**Kuralbaev Erasyl · SE2516**

## Complexity

| Algorithm | Time | Extra space |
|---|---|---|
| DSU find/union | amortized O(alpha(V)) | O(V) |
| Kruskal | O(E log E + E alpha(V)) | O(V+E) |
| Prim with binary heap | O(E log E) | O(V+E) |
| Kosaraju | O(V+E) | O(V+E) |
| Kahn | O(V+E) | O(V) |
| DAG shortest paths | O(V+E) | O(V) |

Kruskal sorts edges then rejects cycles through DSU. Prim grows the cheapest frontier edge using a heap. Kosaraju scans the original and reversed graphs. Kahn removes indegree-zero vertices. DAG paths relax each edge once in topological order.

## MST correctness

For a cut of a weighted graph, a lightest edge crossing that cut is safe for a minimum spanning tree. Kruskal sorts edges by weight and adds only those joining separate DSU components. Each accepted edge is safe across the cut between its two groups, and no cycle can be created. Prim chooses a cheapest edge from the current tree to an unseen vertex. The cut property shows each extension is safe. Repeating from unvisited vertices handles disconnected graphs. Thus both obtain minimum spanning forests with the same total cost.

## DAG correctness

A topological ordering places every predecessor before its successor. All zero-indegree vertices start with distance zero, as if a virtual source were connected to them. Assume preceding vertices already have their optimal distances. Every incoming edge of the current vertex has then been relaxed, so its distance is optimal. Relaxing the current outgoing edges passes optimal candidates to later vertices. Induction proves the result after one topological pass. Negative weights are safe here because there are no cycles. A parent array reconstructs a selected shortest path.

## Choosing algorithms

| Case | Algorithm | Why |
|---|---|---|
| Undirected weighted edge list | Kruskal | Sort and DSU |
| Undirected adjacency list | Prim | Priority queue |
| Directed graph, find mutually reachable groups | Kosaraju | SCCs with two DFS passes |
| Job dependencies, cycle possible | Kahn | Reports cycle or order |
| DAG with negative edge weights | DAG shortest paths | O(V+E), no cycles |

## Empirical benchmark and limitations

The final local ZIP measures 50 rows: 5 algorithms × 10 graph configurations, median of 5 repetitions. Log-log edge-scan slopes: Kruskal 1.0551 sparse and 1.0707 dense; all traversal algorithms 1.0000. Kosaraju scans exactly 2m, Kahn and DAG relaxation scan exactly m. Kruskal additionally performs O(E log E) sort comparisons; the edge-scan curve alone does not show that logarithmic term. Clock times are affected by JVM, warm-up, hardware and background load; counters are the reproducible evidence.

**Unresolved discrepancy:** the official reference table agrees with our generated results on only 1 of the 10 cases. Example, n=100000 and m=2000000: observed MST 3,059,541 and DAG sum 23,453,904, while official values are 3,056,681 and 23,512,087. This must not be presented as an exact reference match. The separately downloadable ZIP contains full CSV, reference comparison and three plots. Seven test method bodies passed a local compatible assertion harness; the actual Maven/JUnit runner remains unverified.
