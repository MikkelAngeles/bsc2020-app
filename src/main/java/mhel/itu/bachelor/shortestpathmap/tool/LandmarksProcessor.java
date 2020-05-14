package mhel.itu.bachelor.shortestpathmap.tool;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;
import mhel.itu.bachelor.shortestpathmap.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LandmarksProcessor {

    public static void processDistToLandmark(IDataModel M, IGraph G, DistanceOracle D, IShortestPathAlgorithm sp, int landmark, String fileName) {
        var Q = new RouteQuery();
        Q.setSource(landmark); //reversed because the edges in the graph are expected to be reversed by the parser.
        Q.addCriterion(EdgeWeightType.DISTANCE, new EdgeProperty("#default", "#default"), 1f);

        long start = System.nanoTime();
        System.out.println("Processing " + M.V() + " distances to landmark vertex" + landmark);

        sp.load(G, D, Q);

        try {
            var fileObj = new File(fileName);
            if (fileObj.createNewFile()) System.out.println("File created: " + fileObj.getName());
            else System.out.println(fileObj.getName() + " already exists. Overriding.");

            var myWriter = new FileWriter(fileName);
            var bw = new BufferedWriter(myWriter);

            bw.write(landmark+"");
            bw.newLine();

            var i = 0;
            long lineStart = System.nanoTime();
            for(var v : M.getVertices()) {
                if(!sp.hasPath(v.I())) continue;
                var dist = sp.distTo(v.I());
                bw.write(v.I() + " " + dist);
                bw.newLine();
                i++;
                if(i % 100 == 0) {
                    var eMs = (((System.nanoTime() - lineStart) / 1000000));
                    var eSec = eMs / 1000;
                    var eMin = eSec / 60;
                    var eHr  = eMin / 60;
                    var eDay = eHr / 24;
                    var eSecondsRounded = (eSec - (eMin * 60));
                    var eMinRounded = eMin - (eHr * 60);
                    var eHrRounded = eHr - (eDay * 60);
                    //System.out.println("Update: Processed " + i + " vertices, "+ (M.V() - i) + " remaining, elapsed time (" +  eHrRounded +"h "+ eMinRounded + "m "+ eSecondsRounded +"s)");

                    var linesDoneAvgTime = eMs / i;
                    var rLines = M.V() - i;
                    var rMs = rLines * linesDoneAvgTime;
                    var rSec = rMs / 1000;
                    var rMin = rSec / 60;
                    var rHr = rMin / 60;
                    var rDay = rHr / 24;
                    var rSecRounded = (rSec - (rMin * 60));
                    var rMinRounded = rMin - (rHr * 60);
                    var rHrRounded = rHr - (rDay * 60);
                    //System.out.println("Est. time remaining (" +  rHrRounded +"h "+ rMinRounded + "m "+ rSecRounded+"s)" );
                }
            }
            bw.close();
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long delta = end - start;
        long ms  = delta / 1000000;
        long sec = ms / 1000;
        long min = sec / 60;
        long hr  = min / 60;
        long d  = min / 60;

        var rSecRounded = (sec - (min * 60));
        var rMinRounded = min - (hr * 60);
        var rHrRounded = hr - (d * 60);
        System.out.println("Done! Processed " + M.V() +" vertices in (" +  rHrRounded +"h "+ rMinRounded + "m "+ rSecRounded+"s)");
    }

    public static void processRandomLandmarksFromJson(int count, String dir, String file) {
        var P   = new GraphParser();
        var M   = P.parseGeoJsonToModel(file, true);
        var G   = M.generateGraph();

        var dOracle = new DistanceOracle(M);

        var D   = new Dijkstra();
        var i = 0;
        for(var l : M.generateRandomLandmarks(count)) {
            var fileName = dir+"/landmarks/dist/landmark-dist-"+i+".txt";
            processDistToLandmark(M, G, dOracle, D, l, fileName);
            i++;
        }
    }

    public static void processLandmarksFromJson(int[] list, String dir, String file) {
        var P   = new GraphParser();
        var M   = P.parseGeoJsonToModel(file, true);
        var G   = M.generateGraph();
        var D   = new Dijkstra();
        var O   = new DistanceOracle(M);
        var i = 0;
        for(var l : list) {
            var fileName = dir+"/landmarks/dist/landmark-dist-"+i+".txt";
            processDistToLandmark(M, G, O, D, l, fileName);
            i++;
        }
    }

    public static void processRandomLandmarksFromDimacs(int count, String dir) {
        var P   = new GraphParser();
        var M   = P.parseFromDimacsPath(dir, true, true);
        var G   = M.generateGraph();
        var D   = new Dijkstra();
        var O   = new DistanceOracle(M);
        var i   = 0;
        for(var l : M.generateRandomLandmarks(count)) {
            var fileName = "resources/dimacs/"+dir+"/landmarks/dist/landmark-dist-"+i+".txt";
            processDistToLandmark(M, G, O, D, l, fileName);
            i++;
        }
    }

    public static void processLandmarksFromDimacs(int[] list, String dir) {
        var P   = new GraphParser();
        var M   = P.parseFromDimacsPath(dir, true, true);
        var G   = M.generateGraph();
        var D   = new Dijkstra();
        var O   = new DistanceOracle(M);
        var i   = 0;
        for(var l : list) {
            var fileName = "resources/dimacs/"+dir+"/landmarks/dist/landmark-dist-"+i+".txt";
            processDistToLandmark(M, G, O, D, l, fileName);
            i++;
        }
    }

    public static void main(String[] args) {
        //processRandomLandmarksFromDimacs(64, "cal");
/*

        processLandmarksFromDimacs(
               new int[] {1041058,
                       226263,
                       266720,
                       49111,
                       384157,
                       763912,
                       112211,
                       579684,
                       633522,
                       594127,
                       632987,
                       178535,
                       188942,
                       447937,
                       136580,
                       510548,
                       813123,
                       784641,
                       305089,
                       490147,
                       206345,
                       977156,
                       983215,
                       270904,
                       289970,
                       33574,
                       657075,
                       248907,
                       245664,
                       1051510,
                       1047915,
                       1021778},
                "fla"
        );
*/

        //processRandomLandmarksFromJson(64, "resources/geojson/hil", "resources/geojson/hil/hil.geojson");
        processLandmarksFromJson(new int[] {23617,
                                            5796,
                                            8337,
                                            6294,
                                            11639,
                                            25615,
                                            20597,
                                            17613,
                                            25744,
                                            27808,
                                            4156,
                                            7735,
                                            10633,
                                            13767,
                                            13369,
                                            3085}, "resources/geojson/hil", "resources/geojson/hil/hil.geojson");
    }
}
