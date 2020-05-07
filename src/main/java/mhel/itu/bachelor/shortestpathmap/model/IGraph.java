package mhel.itu.bachelor.shortestpathmap.model;

import edu.princeton.cs.algs4.Bag;

public interface IGraph {
    int V();
    int E();
    void addEdge(IEdge e);
    Iterable<IEdge> adj(int v);
    Iterable<IEdge> edges();
    Bag<IEdge>[] getAdj();
    void validateVertex(int v);
    String toString();
}
