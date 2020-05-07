package mhel.itu.bachelor.shortestpathmap.algorithm;

import mhel.itu.bachelor.shortestpathmap.model.*;

import java.util.Queue;

public interface IShortestPathAlgorithm {
    double distTo(int v);
    boolean hasPath(int v);
    Iterable<IEdge> pathTo(int v);
    void load(IGraph G, DistanceOracle O, RouteQuery Q);
    Queue<Integer> getVisited();
}
