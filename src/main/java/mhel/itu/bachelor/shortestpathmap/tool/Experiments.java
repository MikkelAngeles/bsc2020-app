package mhel.itu.bachelor.shortestpathmap.tool;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;
import mhel.itu.bachelor.shortestpathmap.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import static edu.princeton.cs.algs4.StdStats.*;
import static edu.princeton.cs.algs4.StdStats.min;

public class Experiments {

    public static void spExperiment(String title, IShortestPathAlgorithm sp, IGraph G, ArrayList<RouteQuery> Q, DistanceOracle D, int n, boolean printProgress) {
        var distArr = new double[n];
        var elapsedArr = new double[n];
        var i = 0;
        for(var q : Q) {
            var start = System.currentTimeMillis();
            sp.load(G, D, q);
            distArr[i] = sp.hasPath(q.getTarget()) ? sp.distTo(q.getTarget()) : 0;
            var end = System.currentTimeMillis();
            elapsedArr[i] = (end - start);
            if(printProgress) System.out.println(timeStamp() + "#" + i + ": " + q.getSource() + " -> " + q.getTarget() + " in " + (end-start) + "ms");
            i++;
        }
        prettyPintVerboseTable(distArr, elapsedArr);
        printFullLaTeXTable("", n, G.V(), G.E() ,distArr, elapsedArr);
        printToLogFile(title, G.V(), G.E(), distArr, elapsedArr);
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

    public static void spDimacsExperiment(String title, IShortestPathAlgorithm alg, String dimacsName, int n, boolean printProgress) {
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

        graphEnd = System.currentTimeMillis();
        System.out.println(timeStamp() + "Graph done loading in "+ (graphEnd - graphStart)+"ms -> V: " + G.V() + " E: " + G.E());
        System.out.println(timeStamp() + "Running " + n + " queries, each with a random source s and target t.");
        var formatTitle = title+"-"+dimacsName+"-dimacs-"+n+"-queries";
        spExperiment(formatTitle, alg, G, generateRandomQueries(n, M.V()), D, n, printProgress);
    }

    public static ArrayList<RouteQuery> generateRandomQueries(int n, int V, RouteCriterion[] c) {
        var rnd = new Random();
        var list = new ArrayList<RouteQuery>();
        for(var i = 0; i < n; i++) {
            var Q = new RouteQuery();
            Q.setSource(rnd.nextInt(V));
            Q.setTarget(rnd.nextInt(V));
            Q.setCriteria(c);
            list.add(Q);
        }
        return list;
    }

    public static ArrayList<RouteQuery> generateRandomQueries(int n, int V) {
        var rnd = new Random();
        var list = new ArrayList<RouteQuery>();
        for(var i = 0; i < n; i++) {
            var Q = new RouteQuery();
            Q.setSource(rnd.nextInt(V));
            Q.setTarget(rnd.nextInt(V));
            list.add(Q);
        }
        return list;
    }


    public static void spJsonExperiment(String title, IShortestPathAlgorithm alg, String fileName, int n, boolean printProgress) {
        var P   = new GraphParser();
        var M   = P.parseJsonModel("resources/json/"+fileName+"/"+fileName+".json");
        var formatTitle = title+"-"+fileName+"-"+n+"";
        buildSpExperiment(formatTitle, alg, M, n, printProgress);
    }

    public static void buildSpExperiment(String title, IShortestPathAlgorithm alg, DataModel M, int n, boolean printProgress) {
        var G   = M.generateGraph();
        var D   = new DistanceOracle(M);
        System.out.println(timeStamp() + "Running " + n + " queries, each with a random source s and target t.");
        spExperiment(title, alg, G, generateRandomQueries(n, M.V()), D, n, printProgress);
    }

    public static void buildSpExperimentWithCriteria(String title, IShortestPathAlgorithm alg, DataModel M, int n, RouteCriterion[] c, boolean printProgress) {
        var G   = M.generateGraph();
        var D   = new DistanceOracle(M);
        System.out.println(timeStamp() + "Running " + n + " queries, each with a random source s and target t.");
        spExperiment(title, alg, G, generateRandomQueries(n, M.V(), c), D, n, printProgress);
    }

    public static void spCriteriaGeoJsonExperiment(String title, IShortestPathAlgorithm alg, String fileName, int n, RouteCriterion[] criteria, boolean printProgress) {
        var P   = new GraphParser();
        var M   = P.parseGeoJsonToModel("resources/geojson/"+fileName+".geojson");
        var formatTitle = title+"-"+fileName+"-"+n+"";
        buildSpExperimentWithCriteria(formatTitle, alg, M, n, criteria, printProgress);
    }

    public static void spCriteriaDimacsExperiment(String title, IShortestPathAlgorithm alg, String dimacsName, int n, RouteCriterion[] criteria,  boolean printProgress) {
        var P   = new GraphParser();
        var M   = P.parseDimacsFromIn(
                new In("resources/dimacs/"+dimacsName+"/vertices.co"),
                new In("resources/dimacs/"+dimacsName+"/distance.gr"),
                new In("resources/dimacs/"+dimacsName+"/time.gr"),
                false);
        var formatTitle = title+"-"+dimacsName+"-"+n+"";
        buildSpExperimentWithCriteria(formatTitle, alg, M, n, criteria, printProgress);
    }

    public static void main(String[] args) {
        //dijkstraSPExperiment(10000, "resources/algs4/", "1000EWD.txt", false);
        //dijkstraSPExperiment(10000, "resources/algs4/", "10000EWD.txt", false);
        //dijkstraSPExperiment(500, "resources/algs4/", "largeEWD.txt", true);
        //dijkstraSPExperiment(5000, new In("resources/algs4/1000EWD.txt"));
        //dijkstraSPExperiment(10000, new In("resources/algs4/1000EWD.txt"));

        //dijkstraSPExtendedAstarSPExperiment(10000, "resources/algs4/", "1000EWD.txt", false);


        //spDimacsExperiment("dijkstra", new Dijkstra(), "fla", 1000, true);
        //spDimacsExperiment("astar-1", new Astar(1), "fla", 1000, true);
        //spDimacsExperiment("astar-10", new Astar(10), "fla", 1000, true);
        //spDimacsExperiment("astar-100", new Astar(100), "fla", 1000, true);

        var criteria = new RouteCriterion[1];
        criteria[0] = new RouteCriterion(EdgeWeightType.TIME, new EdgeProperty("#default", "#default"), 1f);
        //spCriteriaGeoJsonExperiment("dijkstra-criteria-time-default", new Dijkstra(), "hil", 1000, criteria, false);
        spCriteriaDimacsExperiment("dijkstra-criteria-time-default", new Dijkstra(), "nyc", 1000, criteria, true);

        criteria[0] = new RouteCriterion(EdgeWeightType.DISTANCE, new EdgeProperty("#default", "#default"), 1f);
        //spCriteriaGeoJsonExperiment("dijkstra-criteria-time-default", new Dijkstra(), "hil", 1000, criteria, false);
        spCriteriaDimacsExperiment("dijkstra-criteria-distance-default", new Dijkstra(), "nyc", 1000, criteria, true);

       /* var criteria = new RouteCriterion[1];
        criteria[0] = new RouteCriterion(EdgeWeightType.DISTANCE, new EdgeProperty("foot", "yes"), 0f);
        spCriteriaGeoJsonExperiment("dijkstra-criteria-foot-yes-0", new Dijkstra(), "hil", 1000, criteria, false);*/

        //spDimacsExperiment("al-test-1", new AstarLandmarks(1), "nyc", 10000, true);
        //spDimacsExperiment("astar", new Astar(1), "fla", 10000, true);
        /*
        spDimacsExperiment("astar-10", new Astar(10), "nyc", 1000, true);
        spDimacsExperiment("astar-100", new Astar(100), "nyc", 1000, true);
        spDimacsExperiment("astar-1000", new Astar(1000), "nyc", 1000, true);
        spDimacsExperiment("astar-10000", new Astar(10000), "nyc", 1000, true);

         */
        //spJsonExperiment("Dijkstra", new Dijkstra(), "hil", 100000, false);
        //spJsonExperiment("astar-0", new Astar(0), "hil", 10000, false);
        //spJsonExperiment("astar-0.25", new Astar(0.25), "hil", 10000, false);
        //spJsonExperiment("astar-0.5", new Astar(0.5), "hil", 10000, false);
        //spJsonExperiment("astar-0.75", new Astar(0.75), "hil", 10000, false);
        //spJsonExperiment("astar-2.5", new Astar(2.5), "hil", 10000, false);
        //spJsonExperiment(new Astar(1), "hil", 10000, false);
        //spJsonExperiment(new Astar(1.5), "hil", 1000, false);
        //spJsonExperiment(new Astar(2), "hil", 1000, false);
        //dijkstraDimacsExperiment("nyc", 10000, false);
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
        StdOut.printf("           %10s %10s \n", "distance", "time (ms)");
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
        StdOut.println("\\multirow{2}{8em}{"+fileName+"} & \\multirow{2}{4em}{"+v+"} & \\multirow{2}{4em}{"+e+"} & \\multirow{2}{4em}{"+n+"}");

        StdOut.printf("&  %12s  & %10.3f    & %10.3f    & %10.3f  & %10.3f \\\\ \n", "dist" ,min(arr1),mean(arr1),max(arr1),stddev(arr1));
        StdOut.printf("& & & & %12s  & %10.0f    & %10.0f    & %10.0f  & %10.0f \\\\ \n", "time (ms)",min(arr2),mean(arr2),max(arr2),stddev(arr2));

        StdOut.println("\\hline");
    }

    public static String timeStamp() {
        var cal = Calendar.getInstance();
        var timeOnly = new SimpleDateFormat("HH:mm:ss");
        return timeOnly.format(cal.getTime()) + ": ";
    }

    public static void printToLogFile(String title, int v, int e, double[] arr1, double[] arr2) {
        try {
            var file = "logs/experiments/"+title+".txt";
            var fileObj = new File(file);
            if (fileObj.createNewFile()) System.out.println("File created: " + fileObj.getName());
            else System.out.println(fileObj.getName() + " already exists. Overriding.");

            var myWriter = new FileWriter(file);
            var bw = new BufferedWriter(myWriter);
            var n = arr1.length;
            bw.write(title);
            bw.newLine();

            bw.write(n+"");
            bw.newLine();

            bw.newLine();
            bw.write("----------------------------Results---------------------------------");
            bw.newLine();
            bw.write(String.format("           %10s %10s \n", "distance", "time (ms)")+"");
            bw.write(String.format("       min %10.3f%10.3f\n", min(arr1), min(arr2)));
            bw.write(String.format("      mean %10.3f%10.3f\n", mean(arr1),mean(arr2)));
            bw.write(String.format("       max %10.3f%10.3f\n", max(arr1), max(arr2)));
            bw.write(String.format("    stddev %10.3f%10.3f\n", stddev(arr1), stddev(arr2)));
            bw.write(String.format("       var %10.3f%10.3f\n", var(arr1), var(arr2)));
            bw.write(String.format("   stddevp %10.3f%10.3f\n", stddevp(arr1), stddevp(arr2)));
            bw.write(String.format("      varp %10.3f%10.3f\n", varp(arr1), varp(arr2)));
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.write("--------------------------------------------------------------LaTeX table-------------------------------------------------------------------");
            bw.newLine();
            bw.write(String.format("\\multirow{2}{8em}{"+title+"} & \\multirow{2}{4em}{"+v+"} & \\multirow{2}{4em}{"+e+"} & \\multirow{2}{4em}{"+n+"}"));
            bw.newLine();
            bw.write(String.format("&  %12s  & %10.3f    & %10.3f    & %10.3f  & %10.3f \\\\ \n", "dist" ,min(arr1),mean(arr1),max(arr1),stddev(arr1)));
            bw.write(String.format("& & & & %12s  & %10.0f    & %10.0f    & %10.0f  & %10.0f \\\\ \n", "time (ms)",min(arr2),mean(arr2),max(arr2),stddev(arr2)));
            bw.write(String.format("\\hline"));
            bw.newLine();
            bw.close();
            myWriter.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
