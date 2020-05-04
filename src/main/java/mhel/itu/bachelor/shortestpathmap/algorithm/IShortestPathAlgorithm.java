package mhel.itu.bachelor.shortestpathmap.algorithm;

import java.util.Queue;

public interface IShortestPathAlgorithm {
    double distTo(int v);
    boolean hasPath(int v);
    Iterable<IEdge> pathTo(int v);
    void perform(SimpleGraph G, IDataModel M, DistanceOracle d, RouteQuery query);
    Queue<Integer> getVisited();
}
