package mhel.itu.bachelor.shortestpathmap.algorithm;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Stack;

public class DijkstraSPExtendedAstarSP {
    private double[] gScore;
    private double[] fScore;
    private DirectedEdge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    public DijkstraSPExtendedAstarSP(EdgeWeightedDigraph G, int s, int t) {
        for (DirectedEdge e : G.edges()) {
            if (e.weight() < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }

        //Extended A* components
        gScore = new double[G.V()];
        fScore = new double[G.V()];
        edgeTo = new DirectedEdge[G.V()];
        validateVertex(s);

        for (int v = 0; v < G.V(); v++) {
            gScore[v] = Float.POSITIVE_INFINITY;
            fScore[v] = Float.POSITIVE_INFINITY;
        }

        gScore[s] = 0.0f;
        fScore[s] = h(s);

        pq = new IndexMinPQ<>(G.V());
        pq.insert(s, fScore[s]);

        while (!pq.isEmpty()) {
            var current = pq.delMin();
            if(current == t) break;
            for (var e : G.adj(current)) relax(e);
        }
        assert check(G, s);
    }

    private void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        var tentative_gScore = gScore[v] + e.weight();
        if(tentative_gScore < gScore[w]) {
            edgeTo[w] = e;
            gScore[w] = tentative_gScore;
            fScore[w] = gScore[w] + h(w);
            if(!pq.contains(w)) pq.insert(w, fScore[w]);
            else                pq.decreaseKey(w, fScore[w]);
        }
    }

    public double h(int n) {
        return 0;
    }

    public double manhattan(double x1,  double y1, double x2, double y2) {
        var delta_x = Math.abs(x2 - x1);
        var delta_y = Math.abs(y2 - y1);
        return (delta_x + delta_y);
    }

    public double distTo(int v) {
        validateVertex(v);
        return fScore[v];
    }

    public boolean hasPathTo(int v) {
        validateVertex(v);
        return fScore[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<DirectedEdge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }

    private boolean check(EdgeWeightedDigraph G, int s) {
        for (DirectedEdge e : G.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }
        if (fScore[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && fScore[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        for (int v = 0; v < G.V(); v++) {
            for (DirectedEdge e : G.adj(v)) {
                int w = e.to();
                if (fScore[v] + e.weight() < fScore[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            DirectedEdge e = edgeTo[w];
            int v = e.from();
            if (w != e.to()) return false;
            if (fScore[v] + e.weight() != fScore[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    private void validateVertex(int v) {
        int V = fScore.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

}

