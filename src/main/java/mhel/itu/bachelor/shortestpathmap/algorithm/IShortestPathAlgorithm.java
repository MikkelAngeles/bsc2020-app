package mhel.itu.bachelor.shortestpathmap.algorithm;

import java.util.Queue;
import java.util.Set;

public interface IShortestPathAlgorithm {
    double distTo(int v);
    boolean hasPath(int v);
    Iterable<IEdge> pathTo(int v);
    Queue<Integer> getVisited();
    void load(IGraph G, IVertex s, IVertex t, Set<RouteCriteriaEvaluationType> criterias);
    void load(SimpleGraph G, DataModel M, int s, int t);
    void perform(SimpleGraph G, DataModel M, RouteQuery query);
    void setHeuristicWeight(float heuristicWeight);
    long getElapsed();
}
