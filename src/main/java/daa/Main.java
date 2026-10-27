package daa;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class Main  {
    public record Edge(int u,int v,int w) {
    }
    public static class Graph  {
        public final int n;
        public final boolean directed;
        public final ArrayList<Edge>[] adj;
        public final ArrayList<Edge> edges=new ArrayList<>();
        @SuppressWarnings("unchecked") public Graph(int n,boolean directed) {
            if(n<0)throw new IllegalArgumentException();
            this.n=n;
            this.directed=directed;
            adj=(ArrayList<Edge>[])new ArrayList<?>[n];
            for(int i=0;i<n;i++)adj[i]=new ArrayList<>();
        }
        public void add(int u,int v,int w) {
            if(u<0||v<0||u>=n||v>=n)throw new IllegalArgumentException();
            Edge e=new Edge(u,v,w);
            edges.add(e);
            adj[u].add(e);
            if(!directed)adj[v].add(new Edge(v,u,w));
        }
        public static Graph read(Path p)throws IOException {
            try(Scanner s=new Scanner(p)) {
                int n=s.nextInt(),m=s.nextInt();
                String t=s.next();
                if(!t.equals("directed")&&!t.equals("undirected"))throw new IOException("invalid graph type");
                Graph g=new Graph(n,t.equals("directed"));
                for(int i=0;i<m;i++)g.add(s.nextInt(),s.nextInt(),s.nextInt());
                return g;
            }
        }
    }
    public static class Metrics {
        public long dsuOps,edgeScans,comparisons;
        public int maxDepth;
    }
    public static class DSU {
        final int[] p,size;
        int groups;
        final Metrics m;
        public DSU(int n,Metrics m) {
            p=new int[n];
            size=new int[n];
            groups=n;
            this.m=m;
            for(int i=0;i<n;i++) {
                p[i]=i;
                size[i]=1;
            }
        }
        public static DSU makeSet(int n) {
            return new DSU(n,new Metrics());
        }
        public int find(int x) {
            m.dsuOps++;
            int r=x;
            while(p[r]!=r)r=p[r];
            while(p[x]!=x) {
                int next=p[x];
                p[x]=r;
                x=next;
            }
            return r;
        }
        public boolean union(int a,int b) {
            int x=find(a),y=find(b);
            if(x==y)return false;
            if(size[x]<size[y]) {
                int t=x;
                x=y;
                y=t;
            }
            p[y]=x;
            size[x]+=size[y];
            groups--;
            m.dsuOps++;
            return true;
        }
        public int components() {
            return groups;
        }
    }
    public record MST(long weight,int edgeCount,int components) {
    }
    public static MST kruskal(Graph g,Metrics m) {
        if(g.directed)throw new IllegalArgumentException();
        ArrayList<Edge> es=new ArrayList<>(g.edges);
        es.sort((a,b)-> {
            m.comparisons++;return Integer.compare(a.w,b.w);
        }
        );
        DSU d=new DSU(g.n,m);
        long sum=0;
        int count=0;
        for(Edge e:es) {
            m.edgeScans++;
            if(d.union(e.u,e.v)) {
                sum+=e.w;
                count++;
                if(count==g.n-1)break;
            }
        }
        return new MST(sum,count,d.components());
    }
    public static MST prim(Graph g,Metrics m) {
        if(g.directed)throw new IllegalArgumentException();
        boolean[] seen=new boolean[g.n];
        record Item(int v,int w) {
        }
        PriorityQueue<Item> q=new PriorityQueue<>((a,b)-> {
            m.comparisons++;return Integer.compare(a.w,b.w);
        }
        );
        long sum=0;
        int count=0,groups=0;
        for(int s=0;s<g.n;s++) {
            if(seen[s])continue;
            groups++;
            q.add(new Item(s,0));
            while(!q.isEmpty()) {
                Item x=q.poll();
                if(seen[x.v])continue;
                seen[x.v]=true;
                if(x.v!=s) {
                    sum+=x.w;
                    count++;
                }
                for(Edge e:g.adj[x.v]) {
                    m.edgeScans++;
                    if(!seen[e.v])q.add(new Item(e.v,e.w));
                }
            }
        }
        return new MST(sum,count,groups);
    }
    public record SCC(int count,int[] component) {
    }
    private record Frame(int v,int next) {
    }
    public static SCC kosaraju(Graph g,Metrics m) {
        if(!g.directed)throw new IllegalArgumentException();
        boolean[] visited=new boolean[g.n];
        int[] order=new int[g.n];
        int k=0;
        ArrayList<Frame> stack=new ArrayList<>();
        for(int s=0;s<g.n;s++) {
            if(visited[s])continue;
            visited[s]=true;
            stack.add(new Frame(s,0));
            while(!stack.isEmpty()) {
                m.maxDepth=Math.max(m.maxDepth,stack.size());
                int last=stack.size()-1;
                Frame f=stack.get(last);
                if(f.next<g.adj[f.v].size()) {
                    Edge e=g.adj[f.v].get(f.next);
                    stack.set(last,new Frame(f.v,f.next+1));
                    m.edgeScans++;
                    if(!visited[e.v]) {
                        visited[e.v]=true;
                        stack.add(new Frame(e.v,0));
                    }
                }
                else {
                    order[k++]=f.v;
                    stack.remove(last);
                }
            }
        }
        @SuppressWarnings("unchecked") ArrayList<Integer>[] rev=(ArrayList<Integer>[])new ArrayList<?>[g.n];
        for(int i=0;i<g.n;i++)rev[i]=new ArrayList<>();
        for(Edge e:g.edges)rev[e.v].add(e.u);
        int[] comp=new int[g.n];
        Arrays.fill(comp,-1);
        int groups=0;
        ArrayDeque<Integer> st=new ArrayDeque<>();
        for(int i=k-1;i>=0;i--) {
            int s=order[i];
            if(comp[s]>=0)continue;
            comp[s]=groups;
            st.push(s);
            while(!st.isEmpty()) {
                m.maxDepth=Math.max(m.maxDepth,st.size());
                int v=st.pop();
                for(int u:rev[v]) {
                    m.edgeScans++;
                    if(comp[u]<0) {
                        comp[u]=groups;
                        st.push(u);
                    }
                }
            }
            groups++;
        }
        return new SCC(groups,comp);
    }
    public static int[] kahn(Graph g,Metrics m) {
        if(!g.directed)throw new IllegalArgumentException();
        int[] deg=new int[g.n];
        for(Edge e:g.edges)deg[e.v]++;
        ArrayDeque<Integer> q=new ArrayDeque<>();
        for(int i=0;i<g.n;i++)if(deg[i]==0)q.add(i);
        int[] order=new int[g.n];
        int k=0;
        while(!q.isEmpty()) {
            int v=q.remove();
            order[k++]=v;
            for(Edge e:g.adj[v]) {
                m.edgeScans++;
                if(--deg[e.v]==0)q.add(e.v);
            }
        }
        if(k!=g.n)throw new IllegalArgumentException("cycle detected");
        return order;
    }
    public static void validateOrder(Graph g,int[] order) {
        if(order.length!=g.n)throw new IllegalArgumentException();
        int[] pos=new int[g.n];
        boolean[] used=new boolean[g.n];
        for(int i=0;i<g.n;i++) {
            int v=order[i];
            if(v<0||v>=g.n||used[v])throw new IllegalArgumentException();
            used[v]=true;
            pos[v]=i;
        }
        for(Edge e:g.edges)if(pos[e.u]>=pos[e.v])throw new IllegalArgumentException();
    }
    public record Paths(long[] distance,int[] parent) {
        public List<Integer> path(int v) {
            ArrayList<Integer> result=new ArrayList<>();
            for(int x=v;x>=0;x=parent[x])result.add(x);
            Collections.reverse(result);
            return result;
        }
        public long sum() {
            long s=0;
            for(long x:distance)s+=x;
            return s;
        }
    }
    public static Paths dagShortest(Graph g,Metrics m) {
        int[] order=kahn(g,new Metrics());
        int[] indeg=new int[g.n],parent=new int[g.n];
        long[] dist=new long[g.n];
        Arrays.fill(dist,Long.MAX_VALUE/4);
        Arrays.fill(parent,-1);
        for(Edge e:g.edges)indeg[e.v]++;
        for(int i=0;i<g.n;i++)if(indeg[i]==0)dist[i]=0;
        for(int v:order)for(Edge e:g.adj[v]) {
            m.edgeScans++;
            if(dist[v]!=Long.MAX_VALUE/4&&dist[v]+e.w<dist[e.v]) {
                dist[e.v]=dist[v]+e.w;
                parent[e.v]=v;
            }
        }
        return new Paths(dist,parent);
    }
    private static int[] shuffle(int n,Random r) {
        int[] a=new int[n];
        for(int i=0;i<n;i++)a[i]=i;
        for(int i=n-1;i>0;i--) {
            int j=r.nextInt(i+1),t=a[i];
            a[i]=a[j];
            a[j]=t;
        }
        return a;
    }
    public static Graph generate(int n,int m,String kind) {
        Random r=new Random(42);
        Graph g=new Graph(n,!kind.equals("undirected"));
        int[] rank=null;
        if(kind.equals("undirected")) {
            int[] perm=shuffle(n,r);
            for(int i=1;i<n;i++)g.add(perm[r.nextInt(i)],perm[i],1+r.nextInt(1000));
        }
        else if(kind.equals("dag"))rank=shuffle(n,r);
        else if(!kind.equals("directed"))throw new IllegalArgumentException();
        while(g.edges.size()<m) {
            int a=r.nextInt(n),b=r.nextInt(n);
            if(a==b)continue;
            if(rank!=null&&rank[a]>rank[b]) {
                int t=a;
                a=b;
                b=t;
            }
            g.add(a,b,1+r.nextInt(1000));
        }
        return g;
    }
    private interface Runner {
        long run(Graph g,Metrics m);
    }
    private static void row(PrintWriter out,String name,String graph,Graph g,Runner r) {
        long[] times=new long[5];
        Metrics first=null;
        long value=0;
        for(int i=0;i<5;i++) {
            Metrics met=new Metrics();
            long start=System.nanoTime();
            long v=r.run(g,met);
            times[i]=System.nanoTime()-start;
            if(i==0) {
                first=met;
                value=v;
            }
            else if(v!=value)throw new IllegalStateException("non-deterministic");
        }
        Arrays.sort(times);
        out.printf(Locale.ROOT,"%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d%n",name,graph,g.n,g.edges.size(),times[2]/1e6,first.dsuOps,first.edgeScans,first.comparisons,first.maxDepth,value);
    }
    public static void benchmark()throws IOException {
        Files.createDirectories(Path.of("results"));
        try(PrintWriter out=new PrintWriter(Files.newBufferedWriter(Path.of("results/results.csv")))) {
            out.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            for(int n:new int[] {
                1000,3162,10000,31623,100000
            }
            )for(int f:new int[] {
                4,20
            }
            ) {
                int m=n*f;
                String id=n+"_"+m;
                System.out.println(id);
                Graph ug=generate(n,m,"undirected");
                MST a=kruskal(ug,new Metrics()),b=prim(ug,new Metrics());
                if(a.weight!=b.weight||a.edgeCount!=n-1)throw new IllegalStateException("MST mismatch");
                row(out,"Kruskal",id,ug,(g,c)->kruskal(g,c).weight);
                row(out,"Prim",id,ug,(g,c)->prim(g,c).weight);
                ug=null;
                Graph dg=generate(n,m,"directed");
                row(out,"Kosaraju",id,dg,(g,c)->kosaraju(g,c).count);
                dg=null;
                Graph dag=generate(n,m,"dag");
                validateOrder(dag,kahn(dag,new Metrics()));
                row(out,"Kahn",id,dag,(g,c)->kahn(g,c).length);
                row(out,"DagShortestPath",id,dag,(g,c)->dagShortest(g,c).sum());
                out.flush();
            }
        }
    }
    public static void main(String[] args)throws Exception {
        if(args.length==0||args[0].equals("benchmark"))benchmark();
        else if(args[0].equals("read"))System.out.println(Graph.read(Path.of(args[1])).edges.size());
        else throw new IllegalArgumentException("Usage: benchmark | read <file>");
    }
}
