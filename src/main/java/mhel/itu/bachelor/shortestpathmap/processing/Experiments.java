package mhel.itu.bachelor.shortestpathmap.processing;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;

import java.util.ArrayList;
import java.util.Random;

import static edu.princeton.cs.algs4.StdStats.*;

public class Experiments {

    public static void spExperiment(IShortestPathAlgorithm sp, DataModel M, SimpleGraph G, ArrayList<RouteQuery> Q, DistanceOracle D, int n) {
        var distArr = new double[n];
        var elapsedArr = new double[n];
        var i = 0;
        for(var q : Q) {
            var start = System.currentTimeMillis();
            sp.perform(G, M, D, q);
            distArr[i] = sp.hasPath(q.getTarget()) ? sp.distTo(q.getTarget()) : 0;
            var end = System.currentTimeMillis();
            elapsedArr[i] = (end - start);
            i++;
        }
        prettyPintVerboseTable(distArr, elapsedArr);
    }

    public static void dijkstraExperiment(DataModel M, SimpleGraph G, RouteQuery Q, DistanceOracle D, int n) {
        var rnd = new Random();
        var dijkstraDistance = new double[n];
        var dijkstraElapsed = new double[n];
        for(var i = 0; i < n; i++) {
            Q.setSource(rnd.nextInt(M.V()));
            Q.setTarget(rnd.nextInt(M.V()));
            var start = System.currentTimeMillis();
            var dijkstra = new Dijkstra();
            dijkstra.perform(G, M, D, Q);
            dijkstraDistance[i] = dijkstra.hasPath(Q.getTarget()) ? dijkstra.distTo(Q.getTarget()) : 0;
            var end = System.currentTimeMillis();
            dijkstraElapsed[i] = (end - start);
        }

        /*StdOut.println("Dijkstra stats - distTo");
        printVerboseStats(dijkstraDistance);
        StdOut.println();
        StdOut.println("Dijkstra stats - elapsed");
        printVerboseStats(dijkstraElapsed);
        StdOut.println();*/

        prettyPintVerboseTable(dijkstraDistance, dijkstraElapsed);
    }

    public static void printVerboseStats(double[] arr) {
        StdOut.printf("       min %10.3f\n", min(arr));
        StdOut.printf("      mean %10.3f\n", mean(arr));
        StdOut.printf("       max %10.3f\n", max(arr));
        StdOut.printf("    stddev %10.3f\n", stddev(arr));
        StdOut.printf("       var %10.3f\n", var(arr));
        StdOut.printf("   stddevp %10.3f\n", stddevp(arr));
        StdOut.printf("      varp %10.3f\n", varp(arr));
    }

    public static void prettyPintVerboseTable(double[] arr1, double[] arr2) {
        StdOut.printf("       min %10.3f%10.3f\n", min(arr1), min(arr2));
        StdOut.printf("      mean %10.3f%10.3f\n", mean(arr1),mean(arr2));
        StdOut.printf("       max %10.3f%10.3f\n", max(arr1), max(arr2));
        StdOut.printf("    stddev %10.3f%10.3f\n", stddev(arr1), stddev(arr2));
        StdOut.printf("       var %10.3f%10.3f\n", var(arr1), var(arr2));
        StdOut.printf("   stddevp %10.3f%10.3f\n", stddevp(arr1), stddevp(arr2));
        StdOut.printf("      varp %10.3f%10.3f\n", varp(arr1), varp(arr2));
    }

    public static void main(String[] args) {
        var n   = 5000;
        var P   = new GraphParser();
        //var M   = P.parseFromMyJson("resources/json/hil/hil.json");
        var M   = P.parseDimacsFromIn(
                    new In("resources/dimacs/nyc/vertices.co"),
                    new In("resources/dimacs/nyc/distance.gr"),
                    new In("resources/dimacs/nyc/time.gr"),
                false);

        var G   = M.generateGraph();

        var D   = new DistanceOracle(M);

        var rnd = new Random();
        var list = new ArrayList<RouteQuery>();
        for(var i = 0; i < n; i++) {
            var Q = new RouteQuery();
            Q.addCriterion(
                    RouteCriteriaEvaluationType.DISTANCE,
                    new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
                    1f
            );
            Q.setSource(rnd.nextInt(M.V()));
            Q.setTarget(rnd.nextInt(M.V()));
            list.add(Q);
        }
/*
        StdOut.println("Running " + n +" experiments for Dijkstra's");
        spExperiment(new Dijkstra(), M, G, list, D, n);
*/
        StdOut.println("Running " + n +" experiments for A* Landmarks");
        spExperiment(new AstarLandmarks(1), M, G, list, D, n);

        /*
        StdOut.println("Running " + n +" experiments for A*");
        spExperiment(new Astar(100), M, G, list, D, n);
        */

    }


}
