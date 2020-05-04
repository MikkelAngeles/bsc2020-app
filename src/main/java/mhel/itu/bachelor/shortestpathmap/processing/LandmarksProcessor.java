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

    public static void main(String[] args) {
        var L       = 16;
        var pa      = "C:\\Users\\mh89\\dev\\shortest-path-map\\resources\\hi.json";
        var P       = new GraphParser();
        var M       = P.parseFromMyJson(pa);

        M.generateRandomLandmarks(L);

        var G       = M.generateGraph();
        var D       = new Dijkstra();
        var Q       = new RouteQuery();

        Q.addCriterion(
            RouteCriteriaEvaluationType.DISTANCE,
            new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
            1f
        );

        System.out.println("Processing " + M.V() + " distances to " + L + " landmarks.");
        var file = "C:\\Users\\mh89\\dev\\shortest-path-map\\resources\\hi\\";
        var i = 1;
        for(var l : M.getLandmarks()) {
            try {
                var path = file + "landmark"+i+++".txt";
                var myObj = new File(path);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println(myObj.getName() + " already exists. Overriding.");
                }
                var myWriter = new FileWriter(path);
                var bw = new BufferedWriter(myWriter);
                    bw.write(l+"");
                    bw.newLine();
                    bw.write(M.V()+"");
                    bw.newLine();
                    Q.setTarget(l);
                    for(var v : M.getVertices()) {
                        Q.setSource(v.I());
                        D.perform(G, M, Q);
                        var dist = D.hasPath(l) ? D.distTo(l) : Double.POSITIVE_INFINITY;
                        bw.write(v.I() + " " + dist);
                        bw.newLine();
                    }
                bw.close();
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

/*
        for(var v : M.getVertices()) {
            var distTo = new HashMap<Integer, Double>();
            Q.setSource(v.I());
            System.out.println(v.I());
            for(var l : M.getLandmarks()) {
                Q.setTarget(l);
                D.perform(G, M, Q);
                var dist = D.hasPath(l) ? D.distTo(l) : Double.POSITIVE_INFINITY;
                distTo.put(v.I(), dist);
            }

            vertexToLandmarkDistance.put(v.I(), distTo);
        }*/
        System.out.println("Processing complete.");
    }
}
