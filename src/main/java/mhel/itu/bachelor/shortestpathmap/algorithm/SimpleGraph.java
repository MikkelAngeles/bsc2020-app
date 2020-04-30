package mhel.itu.bachelor.shortestpathmap.algorithm;

import edu.princeton.cs.algs4.*;

public class SimpleGraph {
    private final int V;                                 // number of vertices in this digraph
    private int E;                                       // number of edges in this digraph
    private Bag<IEdge>[] adj;                            // adj[v] = adjacency list for vertex v
    private int[] indegree;                              // indegree[v] = indegree of vertex v

    public SimpleGraph(DataModel model) {
        if (model.V() < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        this.V = model.getVertices().length + 1;
        this.E = model.getEdges().length + 1;
        this.indegree = new int[V];
        adj = (Bag<IEdge>[]) new Bag[V];

        for (int v = 0; v < V; v++) adj[v] = new Bag<>();

        for(var i = 0; i < model.getEdges().length; i++) {
            var curr = model.getEdge(i);
            if(curr == null) continue;

            addEdge(curr);
        }
    }

    public void addEdge(SimpleEdge e) {
        validateVertex(e.from().I());
        validateVertex(e.to().I());
        adj[e.from().I()].add(e);
        indegree[e.to().I()]++;
        E++;
    }

    public Iterable<IEdge> adj(int index) {
        validateVertex(index);
        return adj[index];
    }

    public void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " has x,y values out of range 0 and " + (V-1));
    }

    public Iterable<IEdge> edges() {
        var list = new Bag<IEdge>();
        for (int v = 0; v < V; v++) {
            for (var e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    }

    public int getV() {
        return V;
    }

    public int getE() {
        return E;
    }

    public Bag<IEdge>[] getAdj() {
        return adj;
    }

    public int[] getIndegree() {
        return indegree;
    }
}
