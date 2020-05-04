package mhel.itu.bachelor.shortestpathmap.algorithm;

import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.*;

public class Dijkstra implements IShortestPathAlgorithm {
    private IEdge[] edgeTo;
    private double[] distTo;
    private IndexMinPQ<Double> pq;           // priority queue of vertices
    private Queue<Integer> visited;
    private IDataModel dataModel;
    private RouteQuery query;
    private DistanceOracle distanceOracle;

    public Dijkstra() { }

    @Override
    public void perform(SimpleGraph G, IDataModel dm, DistanceOracle d, RouteQuery query) {
        for (var e : G.edges()) {
            if(e == null) continue;
            if (dm.getDist(e.index()) < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }
        visited = new LinkedList<>();
        distTo = new double[G.getV()];          //Assign array size from number of vertices in graph
        edgeTo = new SimpleEdge[G.getV()];     //Assign array size from number of vertices in graph
        this.dataModel = dm;
        this.query = query;
        this.distanceOracle = d;

        //Add POSITIVE_INFINITY value to every vertex
        for (int v = 0; v < G.getV(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;

        //Set the start vertex distance s to 0.0
        distTo[query.getSource()] = 0.0f;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<>(G.getV());
        pq.insert(query.getSource(), distTo[query.getSource()]);

        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (var e : G.adj(v)) {
                relax(e, d.dist(e.index()));
            }
        }
    }

    private void relax(IEdge e, double weight) {
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
        return distTo(v) < Double.POSITIVE_INFINITY;
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
