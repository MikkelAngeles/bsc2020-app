package mhel.itu.bachelor.shortestpathmap.processing;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import static edu.princeton.cs.algs4.StdStats.*;
import static edu.princeton.cs.algs4.StdStats.min;

public class Experiments {

    public static void spExperiment(IShortestPathAlgorithm sp, DataModel M, SimpleGraph G, ArrayList<RouteQuery> Q, DistanceOracle D, int n, boolean printProgress) {
        var distArr = new double[n];
        var elapsedArr = new double[n];
        var i = 0;
        for(var q : Q) {
            var start = System.currentTimeMillis();
            sp.perform(G, M, D, q);
            distArr[i] = sp.hasPath(q.getTarget()) ? sp.distTo(q.getTarget()) : 0;
            var end = System.currentTimeMillis();
            elapsedArr[i] = (end - start);
            if(printProgress) System.out.println(timeStamp() + "#" + i + ": " + q.getSource() + " -> " + q.getTarget() + " in " + (end-start) + "ms");
            i++;
        }
        prettyPintVerboseTable(distArr, elapsedArr);
        printFullLaTeXTable("", n, G.getV(), G.getE() ,distArr, elapsedArr);
    }

    //Experiment used in section about Dijkstra's with 1000EWD.txt, 10000EWD.txt and largeEWD.txt
    public static void dijkstraSPExperiment(int n, String path, String fileName, boolean printProgress) {
        var rnd = new Random();
        var dijkstraDistance = new double[n];
        var dijkstraElapsed = new double[n];

        long graphStart = System.currentTimeMillis();
        if(printProgress) System.out.println(timeStamp() + "Loading input " + (path + fileName) + " to EdgeWeightedDigraph");

        var graph = new EdgeWeightedDigraph(new In(path + fileName));

        long graphEnd = System.currentTimeMillis();

        System.out.println(timeStamp() + "EdgeWeightedDigraph done loading in "+ (graphEnd - graphStart)+"ms -> V: " + graph.V() + " E: " + graph.E());
        System.out.println(timeStamp() + "Running " + n + " queries, each with a random source s and target t.");
        for(var i = 0; i < n; i++) {
            var s = rnd.nextInt(graph.V());
            var t = rnd.nextInt(graph.V());
            var start = System.currentTimeMillis();

            var dijkstra = new DijkstraSP(graph, s);
            dijkstraDistance[i] = dijkstra.hasPathTo(t) ? dijkstra.distTo(t) : 0;

            var end = System.currentTimeMillis();
            dijkstraElapsed[i] = (end - start);
            if(printProgress) System.out.println(timeStamp() + "#" + i + ": " + s + " -> " + t + " in " + (end-start) + "ms");
        }
        printFullLaTeXTable(fileName, n, graph.V(), graph.E() ,dijkstraDistance, dijkstraElapsed);
    }

    //Experiment used in section about A* with 1000EWD.txt, 10000EWD.txt and largeEWD.txt
    public static void dijkstraSPExtendedAstarSPExperiment(int n, String path, String fileName, boolean printProgress) {
        var rnd = new Random();
        var dijkstraDistance = new double[n];
        var dijkstraElapsed = new double[n];

        long graphStart = System.currentTimeMillis();
        System.out.println(timeStamp() + "Loading input " + (path + fileName) + " to EdgeWeightedDigraph");
        var graph = new EdgeWeightedDigraph(new In(path + fileName));

        long graphEnd = System.currentTimeMillis();

        System.out.println(timeStamp() + "EdgeWeightedDigraph done loading in "+ (graphEnd - graphStart)+"ms -> V: " + graph.V() + " E: " + graph.E());
        System.out.println(timeStamp() + "Running " + n + " queries, each with a random source s and target t.");
        for(var i = 0; i < n; i++) {
            var s = rnd.nextInt(graph.V());
            var t = rnd.nextInt(graph.V());
            var start = System.currentTimeMillis();

            var dijkstra = new DijkstraSPExtendedAstarSP(graph, s, t);
            dijkstraDistance[i] = dijkstra.hasPathTo(t) ? dijkstra.distTo(t) : 0;

            var end = System.currentTimeMillis();
            dijkstraElapsed[i] = (end - start);
            if(printProgress) System.out.println(timeStamp() + "#" + i + ": " + s + " -> " + t + " in " + (end-start) + "ms");
        }
        printFullLaTeXTable(fileName, n, graph.V(), graph.E() ,dijkstraDistance, dijkstraElapsed);
    }


    public static void dijkstraDimacsExperiment(String dimacsName, int n, boolean printProgress) {
        var P   = new GraphParser();

        long graphStart = System.currentTimeMillis();
        System.out.println(timeStamp() +"Parsing files in " + dimacsName + "/");
        var M   = P.parseDimacsFromIn(
                new In("resources/dimacs/"+dimacsName+"/vertices.co"),
                new In("resources/dimacs/"+dimacsName+"/distance.gr"),
                new In("resources/dimacs/"+dimacsName+"/time.gr"),
                false);

        long graphEnd = System.currentTimeMillis();
        System.out.println(timeStamp() + "Done parsing in " + (graphEnd - graphStart)+"ms");

        graphStart = System.currentTimeMillis();
        System.out.println(timeStamp() + "Generating graph..");
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
        graphEnd = System.currentTimeMillis();
        if(printProgress) System.out.println(timeStamp() + "Graph done loading in "+ (graphEnd - graphStart)+"ms -> V: " + G.getV() + " E: " + G.getE());
        if(printProgress) System.out.println(timeStamp() + "Running " + n + " queries, each with a random source s and target t.");
        spExperiment(new Dijkstra(), M, G, list, D, n, true);
    }

    public static void main(String[] args) {

        //dijkstraSPExperiment(10000, "resources/algs4/", "1000EWD.txt", false);
        //dijkstraSPExperiment(10000, "resources/algs4/", "10000EWD.txt", false);
        //dijkstraSPExperiment(500, "resources/algs4/", "largeEWD.txt", true);
        //dijkstraSPExperiment(5000, new In("resources/algs4/1000EWD.txt"));
        //dijkstraSPExperiment(10000, new In("resources/algs4/1000EWD.txt"));

        //dijkstraSPExtendedAstarSPExperiment(10000, "resources/algs4/", "1000EWD.txt", false);

        dijkstraDimacsExperiment("nyc", 10000, true);

     /*   var n   = 5000;
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
*//*
        StdOut.println(timeStamp() + "Running " + n +" experiments for Dijkstra's");
        spExperiment(new Dijkstra(), M, G, list, D, n);
*//*
        StdOut.println(timeStamp() + "Running " + n +" experiments for A* Landmarks");
        spExperiment(new AstarLandmarks(1), M, G, list, D, n);

        *//*
        StdOut.println(timeStamp() + "Running " + n +" experiments for A*");
        spExperiment(new Astar(100), M, G, list, D, n);
        *//*
*/
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

    public static void printLaTeXTable(int n, double[] arr1, double[] arr2) {
        StdOut.printf("%10s    & %12s  &%10s     & %10s    & %10s  &  %10s\n", "n", "metric","min","mean","max","stddev");
        StdOut.printf("%10d    & %12s  & %10.3f    & %10.3f    & %10.3f  & %10.3f\n", n, "dist" ,min(arr1),mean(arr1),max(arr1),stddev(arr1));
        StdOut.printf("%10d    & %12s  & %10.0f    & %10.0f    & %10.0f  & %10.0f\n", n, "time (ms)",min(arr2),mean(arr2),max(arr2),stddev(arr2));
    }

    //Based on my LaTeX report design.
    public static void printFullLaTeXTable(String fileName, int n, int v, int e, double[] arr1, double[] arr2) {
        StdOut.println("\\multirow{2}{12em}{"+fileName+"} & \\multirow{2}{4em}{"+v+"} & \\multirow{2}{4em}{"+e+"} & \\multirow{2}{4em}{"+n+"}");

        StdOut.printf("&  %12s  & %10.3f    & %10.3f    & %10.3f  & %10.3f \\\\ \n", "dist" ,min(arr1),mean(arr1),max(arr1),stddev(arr1));
        StdOut.printf("& & & & %12s  & %10.0f    & %10.0f    & %10.0f  & %10.0f \\\\ \n", "time (ms)",min(arr2),mean(arr2),max(arr2),stddev(arr2));

        StdOut.println("\\hline");
    }

    public static String timeStamp() {
        var cal = Calendar.getInstance();
        var timeOnly = new SimpleDateFormat("HH:mm:ss");
        return timeOnly.format(cal.getTime()) + ": ";
    }


}
