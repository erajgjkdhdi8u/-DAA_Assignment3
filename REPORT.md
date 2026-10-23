# Assignment 3 Report — Graph Algorithms

**Kuralbaev Erasyl — SE2516**

## Algorithms and complexity

| Algorithm | Time | Extra space | Why |
|---|---|---|---|
| DSU find / union | Amortized O(α(V)) | O(V) for parent/size | Path compression and union by size |
| Kruskal | O(E log E + Eα(V)) | O(V + E) | Sort all edges then DSU checks |
| Prim (binary heap) | O(E log E) | O(V + E) | Each adjacency edge can enter the heap |
| Kosaraju | O(V + E) | O(V + E) | DFS, transpose, DFS |
| Kahn | O(V + E) | O(V) | In-degrees and queue |
| DAG shortest paths | O(V + E) | O(V) | One topological pass and edge relaxations |

## Correctness: cut property

For any cut separating vertices, a minimum-weight crossing edge is safe for some minimum spanning tree. Kruskal examines edges in increasing weight order, so when it joins two different components, the selected edge is a minimum crossing edge for the relevant cut. The DSU rejects every edge whose endpoints are already connected, avoiding cycles. Therefore each accepted edge preserves the possibility of completing a minimum spanning forest. Prim also uses the cut property: its priority queue chooses a lightest edge leaving the currently reached vertices. When a graph has several components, running Prim again at an unseen vertex produces a forest. Both algorithms can select different equal-weight edges yet must agree on the forest's total weight.

## Correctness: DAG shortest paths

A topological ordering places every predecessor before its successor. Every vertex with in-degree zero receives distance zero, representing a virtual super-source. Before processing a vertex, all incoming edges have already been relaxed by predecessors. Thus its distance is already the minimum possible from any source, assuming earlier vertices are correct. Relaxing its outgoing edges propagates the best distances to later vertices. Induction on the topological ordering proves that one pass computes all shortest distances. Negative weights are safe because no directed cycles exist, so a later path cannot return to an earlier vertex and invalidate it. Keeping the parent of each improved distance restores the chosen path.

## Which algorithm when

| Situation | Choice | Reason |
|---|---|---|
| Weighted undirected edge list, MST | Kruskal | Sorting + DSU naturally uses an edge list |
| Connected adjacency-list graph, MST | Prim | Priority queue grows the tree locally |
| Strongly connected groups in directed graph | Kosaraju | Two linear passes |
| Task dependencies that must be acyclic | Kahn | Produces order and detects cycles |
| Shortest path with negative weights on DAG | DAG shortest paths | Linear work, supports negative edges |
| General directed graph with cycles | Do not use DAG shortest paths | It requires a topological order |

## Counters and experimental status

The algorithms increment counters for DSU work, scanned edges, weight comparisons, and explicit-stack maximum depth. Kosaraju scans exactly 2E adjacency edges across its passes. Kahn and DAG relaxation scan E adjacency edges each. Comparing edgeScans/(V+E) and edgeScans/E at increasing sizes tests linear growth; Kruskal additionally incurs sorting comparisons O(E log E).

**Verification limitation:** The earlier local benchmark completed only 45 of 50 rows, and several reference values were not matched. A completed CSV, experimental slopes, timings, and plots should not be claimed as validated until the generator and benchmark have been corrected and rerun. The Maven tests in this GitHub repository have not been independently executed via GitHub.
