package mhel.itu.bachelor.shortestpathmap.algorithm;

import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Double.isNaN;


public class AstarLandmarks implements IShortestPathAlgorithm {
    private IEdge[] edgeTo;
    private double[] gScore;
    private double[] fScore;
    private IndexMinPQ<Double> pq;
    private Queue<Integer> visited;
    private IDataModel dataModel;
    private RouteQuery query;
    private double heuristicWeight;
    private DistanceOracle distanceOracle;
    public AstarLandmarks(double h) { this.heuristicWeight = h;}

    @Override
    public void perform(SimpleGraph G, IDataModel dm, DistanceOracle d, RouteQuery query) {
        for (var e : G.edges()) {
            if(e == null) continue;
            if (dm.getDist(e.index()) < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }
        this.dataModel = dm;
        this.query = query;
        this.distanceOracle = d;
        visited = new LinkedList<>();
        edgeTo = new SimpleEdge[G.getV()];     //Assign array size from number of vertices in graph

        gScore = new double[G.getV()]; //Assign array size from number of vertices in graph
        fScore = new double[G.getV()];

        for (int v = 0; v < G.getV(); v++) {
            gScore[v] = Float.POSITIVE_INFINITY;
            fScore[v] = Float.POSITIVE_INFINITY;
        }

        //Set the start vertex distance s to 0.0
        gScore[query.source] = 0.0f;
        fScore[query.source] = h(query.source);

        pq = new IndexMinPQ<>(G.getV());
        pq.insert(query.source, fScore[query.source]);

        while (!pq.isEmpty()) {
            var current = pq.delMin();
            if(current == query.target) break;
            for (var e : G.adj(current)) relax(e);
        }
    }

    private void relax(IEdge e) {
        int v = e.from().I(), w = e.to().I();
        visited.add(w);
        double tentative_gScore = gScore[v] + distanceOracle.dist(e.index());
        if(tentative_gScore < gScore[w]) {
            edgeTo[w] = e;
            gScore[w] = tentative_gScore;
            fScore[w] = gScore[w] + h(w);
            if(!pq.contains(w)) pq.insert(w, fScore[w]);
            else if(pq.keyOf(w) != fScore[w]) pq.decreaseKey(w, fScore[w]);
        }
    }

    public double h(int n) {
        var rs =  distanceOracle.landmarkDist(n, query.target) * heuristicWeight;
        if(rs < 0) throw new IllegalArgumentException("Heuristic value cannot be negative");
        return rs;
    }

    @Override
    public double distTo(int v) {
        return gScore[v];
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