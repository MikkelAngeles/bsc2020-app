package mhel.itu.bachelor.shortestpathmap.processing;

import edu.princeton.cs.algs4.In;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LandmarksProcessor {

    public static void processDistToLandmark(IDataModel M, SimpleGraph G, IShortestPathAlgorithm sp, int target, String dir) {
        var Q = new RouteQuery();
        Q.setTarget(target);
        Q.addCriterion(
                RouteCriteriaEvaluationType.DISTANCE,
                new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
                1f
        );

        long start = System.nanoTime();
        System.out.println("Processing " + M.V() + " distances to landmark vertex" + target);

        try {
            var filePath = dir + "tmplandmark"+target+".txt";
            var fileObj = new File(filePath);
            if (fileObj.createNewFile()) System.out.println("File created: " + fileObj.getName());
            else System.out.println(fileObj.getName() + " already exists. Overriding.");

            var myWriter = new FileWriter(filePath);
            var bw = new BufferedWriter(myWriter);

            bw.write(target+"");
            bw.newLine();
            bw.write(M.V()+"");
            bw.newLine();

            for(var v : M.getVertices()) {
                Q.setSource(v.I());
                sp.perform(G, M, Q);

                //Consider skipping line entirely if there is no distance.
                var dist = sp.hasPath(target) ? sp.distTo(target) : Double.POSITIVE_INFINITY;
                bw.write(v.I() + " " + dist);
                bw.newLine();
            }
            bw.close();
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println("Done! Processed " + M.V() +" vertices in (" + (end-start/1000000) + " ms)");
    }

    public static void createLandmarksHil() {
        var dir     = "resources/json/hil/";
        var pa      = dir + "hil.json";
        var list    = new int[] {22667, 28460, 8231, 20792, 9840, 26338, 8422, 3703, 1962, 22583, 27380, 18166, 15417, 1757, 14, 22970};

        for(var l : list) {
            Runnable task = () -> {
                var P = new GraphParser();
                var M = P.parseFromMyJson(pa);
                var G = M.generateGraph();
                var D = new Dijkstra();
                processDistToLandmark(M, G, D, l, dir);
            };
            var t = new Thread(task);
            new Thread(t).start();
        }
    }

    public static class RunnableProcessing implements Runnable {
        @Override
        public void run() {


        }
    }

    public static void main(String[] args) {

        createLandmarksHil();

    }
}
