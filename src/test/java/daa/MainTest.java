package daa;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
class MainTest {
 @Test void dsu(){Main.DSU d=Main.DSU.makeSet(5);assertEquals(5,d.components());assertTrue(d.union(0,1));assertTrue(d.union(1,2));assertEquals(d.find(0),d.find(2));assertFalse(d.union(0,2));assertEquals(3,d.components());}
 @Test void mstAgreement(){Random r=new Random(42);for(int k=0;k<100;k++){Main.Graph g=new Main.Graph(20,false);for(int i=1;i<20;i++)g.add(r.nextInt(i),i,r.nextInt(100));for(int i=0;i<100;i++){int a=r.nextInt(20),b=r.nextInt(20);g.add(a,b,r.nextInt(100));}Main.MST a=Main.kruskal(g,new Main.Metrics()),b=Main.prim(g,new Main.Metrics());assertEquals(a.weight(),b.weight());assertEquals(19,a.edgeCount());}}
 @Test void disconnected(){Main.Graph g=new Main.Graph(4,false);g.add(0,1,3);assertEquals(4,Main.kruskal(g,new Main.Metrics()).components());assertEquals(3,Main.prim(g,new Main.Metrics()).components());}
 @Test void cycle(){Main.Graph g=new Main.Graph(3,true);g.add(0,1,1);g.add(1,2,1);g.add(2,0,1);assertThrows(IllegalArgumentException.class,()->Main.kahn(g,new Main.Metrics()));assertEquals(1,Main.kosaraju(g,new Main.Metrics()).count());}
 @Test void negativeDag(){Main.Graph g=new Main.Graph(3,true);g.add(0,1,-3);g.add(1,2,2);g.add(0,2,5);int[] order=Main.kahn(g,new Main.Metrics());Main.validateOrder(g,order);Main.Paths p=Main.dagShortest(g,new Main.Metrics());assertEquals(-1,p.distance()[2]);assertEquals(List.of(0,1,2),p.path(2));}
 @Test void stackSafe(){Main.Graph g=new Main.Graph(100000,true);for(int i=0;i<99999;i++)g.add(i,i+1,1);assertEquals(100000,Main.kosaraju(g,new Main.Metrics()).count());}
}