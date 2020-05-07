package mhel.itu.bachelor.shortestpathmap.algorithm;

import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Stack;
import mhel.itu.bachelor.shortestpathmap.model.*;

import java.util.LinkedList;
import java.util.Queue;


public class AstarLandmarks implements IShortestPathAlgorithm {
    private IEdge[] edgeTo;
    private double[] gScore;
    private double[] fScore;
    private IndexMinPQ<Double> pq;
    private RouteQuery Q;
    private DistanceOracle O;
    private double heuristicWeight;
    private Queue<Integer> visited;

    public AstarLandmarks(double h) { this.heuristicWeight = h;}

    @Override
    public void load(IGraph G, DistanceOracle O, RouteQuery Q) {
        for (var e : G.edges()) {
            if(e == null) continue;
            if (O.dist(e.index()) < 0) throw new IllegalArgumentException("edge " + e + " has negative weight");
        }

        this.Q      = Q;
        this.O      = O;
        visited = new LinkedList<>();
        edgeTo = new SimpleEdge[G.V()];     //Assign array size from number of vertices in graph

        gScore = new double[G.V()]; //Assign array size from number of vertices in graph
        fScore = new double[G.V()];

        for (int v = 0; v < G.V(); v++) {
            gScore[v] = Float.POSITIVE_INFINITY;
            fScore[v] = Float.POSITIVE_INFINITY;
        }

        //Set the start vertex distance s to 0.0
        gScore[Q.getSource()] = 0.0f;
        fScore[Q.getSource()] = h(Q.getSource());

        pq = new IndexMinPQ<>(G.V());
        pq.insert(Q.getSource(), fScore[Q.getSource()]);

        while (!pq.isEmpty()) {
            var current = pq.delMin();
            if(current == Q.getTarget()) break;
            for (var e : G.adj(current)) relax(e);
        }
    }

    private void relax(IEdge e) {
        int v = e.from().I(), w = e.to().I();
        visited.add(w);
        double tentative_gScore = gScore[v] + O.dist(e.index());
        if(tentative_gScore < gScore[w]) {
            edgeTo[w] = e;
            gScore[w] = tentative_gScore;
            fScore[w] = gScore[w] + h(w);
            if(!pq.contains(w)) pq.insert(w, fScore[w]);
            else if(pq.keyOf(w) != fScore[w]) pq.decreaseKey(w, fScore[w]);
        }
    }

    public double h(int n) {
        var rs =  O.landmarkDist(n, Q.getTarget()) * heuristicWeight;
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