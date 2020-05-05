package mhel.itu.bachelor.shortestpathmap.processing;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LandmarksProcessor {

    public static void processDistToLandmark(IDataModel M, SimpleGraph G, DistanceOracle D, IShortestPathAlgorithm sp, int landmark, String fileName) {
        var Q = new RouteQuery();
        Q.setSource(landmark); //reversed because the edges in the graph are expected to be reversed by the parser.

        Q.addCriterion(
                RouteCriteriaEvaluationType.DISTANCE,
                new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
                1f
        );

        long start = System.nanoTime();
        System.out.println("Processing " + M.V() + " distances to landmark vertex" + landmark);

        sp.perform(G, M, D, Q);

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
                    System.out.println("Update: Processed " + i + " vertices, "+ (M.V() - i) + " remaining, elapsed time (" +  eHrRounded +"h "+ eMinRounded + "m "+ eSecondsRounded +"s)");

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
                    System.out.println("Est. time remaining (" +  rHrRounded +"h "+ rMinRounded + "m "+ rSecRounded+"s)" );
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
        var pa  = dir + file;
        var P   = new GraphParser();
        var M   = P.parseFromMyJsonReverseEdges(pa);
        var G   = M.generateGraph();
        M.generateRandomLandmarks(count);

        var dOracle = new DistanceOracle(M);

        var D   = new Dijkstra();
        var i = 0;
        for(var l : M.getLandmarks()) {
            var fileName = dir+"/landmarks/dist/landmark-dist-"+i+".txt";
            processDistToLandmark(M, G, dOracle, D, l, fileName);
            i++;
        }
    }

    public static void processLandmarksFromJson(int[] list, String dir, String file) {
        var pa  = dir + file;
        var P   = new GraphParser();
        var M   = P.parseFromMyJsonReverseEdges(pa);
        var G   = M.generateGraph();
        var D   = new Dijkstra();
        var dOracle = new DistanceOracle(M);

        for(var l : list) {
            processDistToLandmark(M, G, dOracle, D, l, dir);
        }
    }

    public static void processRandomLandmarksFromDimacs(int count, String dir) {
        var P   = new GraphParser();
        var M   = P.parseFromDimacsPath(dir, true);
        var G   = M.generateGraph();
        var D   = new Dijkstra();
        M.generateRandomLandmarks(count);

        var dOracle = new DistanceOracle(M);

        var i = 0;
        for(var l : M.getLandmarks()) {
            var fileName = dir+"/landmarks/dist/landmark-dist-"+i+".txt";
            processDistToLandmark(M, G, dOracle, D, l, fileName);
            i++;
        }
    }

    public static void processLandmarksFromDimacs(int[] list, String dir) {
        var P   = new GraphParser();
        var M   = P.parseFromDimacsPath(dir, true);
        var G   = M.generateGraph();
        var D   = new Dijkstra();

        var dOracle = new DistanceOracle(M);

        var i = 0;
        for(var l : list) {
            var fileName = dir+"/landmarks/dist/landmark-dist-"+i+".txt";
            processDistToLandmark(M, G, dOracle, D, l, fileName);
            i++;
        }
    }

    public static void main(String[] args) {
        processRandomLandmarksFromDimacs(32, "fla");

        /*processLandmarksFromDimacs(
               new int[] {1041107, 1383, 246729, 755529, 634661, 179673, 925834, 165624, 977155, 289620, 1613, 1058300, 889217, 49092, 579684, 269613},
                "fla"
        );*/

        //processRandomLandmarksFromJson(16, "resources/json/hil/", "hil.json");
        //processLandmarksFromJson(new int[] {22667, 28460, 8231, 20792, 9840, 26338, 8422, 3703, 1962, 22583, 27380, 18166, 15417, 1757, 14, 22970}, "resources/json/hil/", "hil.json");
    }
}
