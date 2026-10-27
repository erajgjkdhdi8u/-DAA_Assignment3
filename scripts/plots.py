import csv
import collections
from pathlib import Path
import matplotlib.pyplot as plt

# Load the measured benchmark results.
with open("results/results.csv", newline="") as file:
    rows = list(csv.DictReader(file))

for row in rows:
    for key in ("n", "m", "dsu_ops", "edge_scans", "comparisons"):
        row[key] = int(row[key])

groups = collections.defaultdict(list)
for row in rows:
    density = row["m"] // row["n"]
    groups[(row["algorithm"], density)].append(row)

folder = Path("results/plots")
folder.mkdir(parents=True, exist_ok=True)

# Plot 1: Operations versus vertices.
fig, ax = plt.subplots(figsize=(10, 6))
for (algorithm, density), items in groups.items():
    items.sort(key=lambda x: x["n"])
    x = [r["n"] for r in items]
    y = [r["dsu_ops"] + r["edge_scans"] + r["comparisons"] for r in items]
    ax.plot(x, y, marker="o", label=f"{algorithm} {density}n")
ax.set(xlabel="n", ylabel="Counted operations", title="Operations vs n")
ax.set_xscale("log")
ax.set_yscale("log")
ax.legend(fontsize=8, ncol=2)
fig.tight_layout()
fig.savefig(folder / "operations_vs_n.png", dpi=140)
plt.close(fig)

# Plot 2: Scans per edge.
fig, ax = plt.subplots(figsize=(10, 6))
for (algorithm, density), items in groups.items():
    items.sort(key=lambda x: x["n"])
    ax.plot([r["n"] for r in items], [r["edge_scans"] / r["m"] for r in items],
            marker="o", label=f"{algorithm} {density}n")
ax.set(xlabel="n", ylabel="edgeScans / m", title="Scans per edge")
ax.set_xscale("log")
ax.legend(fontsize=8, ncol=2)
fig.tight_layout()
fig.savefig(folder / "scans_per_edge.png", dpi=140)
plt.close(fig)

# Plot 3: Kruskal share of examined edges.
fig, ax = plt.subplots(figsize=(9, 5))
for density in (4, 20):
    items = sorted(groups[("Kruskal", density)], key=lambda x: x["n"])
    ax.plot([r["n"] for r in items], [r["edge_scans"] / r["m"] for r in items],
            marker="o", label=f"m={density}n")
ax.set(xlabel="n", ylabel="edgeScans / m",
       title="Kruskal: share of edges examined")
ax.set_xscale("log")
ax.legend()
fig.tight_layout()
fig.savefig(folder / "kruskal_share.png", dpi=140)
plt.close(fig)
print("Saved three plots")
