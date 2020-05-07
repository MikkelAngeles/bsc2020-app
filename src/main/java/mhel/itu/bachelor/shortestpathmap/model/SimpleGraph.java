package mhel.itu.bachelor.shortestpathmap.model;

import edu.princeton.cs.algs4.*;

public class SimpleGraph implements IGraph {
    private final int V;                                 // number of vertices in this digraph
    private int E;                                       // number of edges in this digraph
    private Bag<IEdge>[] adj;                            // adj[v] = adjacency list for vertex v

    public SimpleGraph(DataModel model) {
        if (model.V() < 0) throw new IllegalArgumentException("Number of vertices must be non-negative");
        if (model.E() < 0) throw new IllegalArgumentException("Number of edges must be non-negative");

        this.V  = model.V();
        this.E  = model.E();
        adj     = model.getAdjacencyTable();
    }

    public SimpleGraph(int v, IEdge[] edges) {
        if (v < 0) throw new IllegalArgumentException("Number of vertices must be non-negative");

        this.V  = v;
        this.E  = edges.length;
        adj     = (Bag<IEdge>[]) new Bag[V];

        for (int i = 0; i < V; i++) adj[i] = new Bag<>();
        for(var edge : edges) addEdge(edge);
    }

    @Override
    public void addEdge(IEdge e) {
        validateVertex(e.from().I());
        validateVertex(e.to().I());
        adj[e.from().I()].add(e);
        E++;
    }

    @Override
    public Iterable<IEdge> adj(int index) {
        validateVertex(index);
        return adj[index];
    }

    @Override
    public void validateVertex(int v) {
        if (v < 0 || v >= V) throw new IllegalArgumentException("vertex " + v + " has x,y values out of range 0 and " + (V-1));
    }

    @Override
    public Iterable<IEdge> edges() {
        var list = new Bag<IEdge>();
        for (int v = 0; v < V; v++) {
            for (var e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    public int V() {
        return V;
    }

    @Override
    public int E() {
        return E;
    }

    @Override
    public Bag<IEdge>[] getAdj() {
        return adj;
    }
}
