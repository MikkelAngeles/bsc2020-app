package mhel.itu.bachelor.shortestpathmap.algorithm;

public interface IGraph {
    int V();
    int E();
    void addEdge(IEdge e);
    Iterable<IEdge> adj(IVertex v);
    Iterable<IEdge> adj(int v);
    Iterable<IEdge> edges();
    void validateVertex(int v);
    int outdegree(int v);
    int indegree(int v);
    void setAdjBounds(int lower, int upper);
    String toString();
}
