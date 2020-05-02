package mhel.itu.bachelor.shortestpathmap.algorithm;

import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.*;

public class Dijkstra implements IShortestPathAlgorithm {
    private IEdge[] edgeTo;
    private float[] distTo;
    private IndexMinPQ<Float> pq;           // priority queue of vertices
    private Queue<Integer> visited;
    private IDataModel dataModel;
    private RouteQuery query;

    public Dijkstra() { }

    @Override
    public void perform(SimpleGraph G, IDataModel dm, RouteQuery query) {
        for (var e : G.edges()) {
            if(e == null) continue;
            if (dm.getDist(e.index()) < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }
        visited = new LinkedList<>();
        distTo = new float[G.getV()];          //Assign array size from number of vertices in graph
        edgeTo = new SimpleEdge[G.getV()];     //Assign array size from number of vertices in graph
        this.dataModel = dm;
        this.query = query;

        //Add POSITIVE_INFINITY value to every vertex
        for (int v = 0; v < G.getV(); v++)
            distTo[v] = Float.POSITIVE_INFINITY;

        //Set the start vertex distance s to 0.0
        distTo[query.getSource()] = 0.0f;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<>(G.getV());
        pq.insert(query.getSource(), distTo[query.getSource()]);
        var found = false;

        while (!pq.isEmpty() && !found) {
            int v = pq.delMin();
            if(v == query.getTarget())  {
                found = true;
                continue;
            }
            for (var e : G.adj(v)) {
                relax(e, cost(e.index()));
            }
        }
    }

    public float cost(int edge) {
        var accum = 0f;
        for(var c : query.getCriteria()) {
            if(dataModel.hasPropType(edge, c.getSet().getType())) {
                if(RouteCriteriaEvaluationType.DISTANCE == c.getEvaluationType()) {
                    accum += dataModel.getDist(edge) * c.getWeightFactor();
                } else if (RouteCriteriaEvaluationType.TIME == c.getEvaluationType()) {
                    accum += dataModel.getTravelTime(edge) * c.getWeightFactor();
                } else if (RouteCriteriaEvaluationType.COUNT == c.getEvaluationType()) {
                    accum += c.getWeightFactor();
                }
            }
        }
        return accum;
    }

    private void relax(IEdge e, float weight) {
        int v = e.from().I(), w = e.to().I();
        visited.add(w);
        if (distTo[w] > distTo[v] + weight) {
            distTo[w] = distTo[v] + weight;
            edgeTo[w] = e;
            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo[w]);
            }
            else  {
                pq.insert(w, distTo[w]);
            }
        }
    }

    @Override
    public double distTo(int v) {
        return distTo[v];
    }

    @Override
    public boolean hasPath(int v) {
        return distTo(v) < Float.POSITIVE_INFINITY;
    }

    @Override
    public Iterable<IEdge> pathTo(int v) {
        if(!hasPath(v)) return null;
        var path = new Stack<IEdge>();
        for (var e = edgeTo[v]; e != null; e = edgeTo[e.from().I()]) {
            path.push(e);
        }
        return path;
    }

    @Override
    public Queue<Integer> getVisited() {
        return visited;
    }
}
