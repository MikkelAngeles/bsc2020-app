package mhel.itu.bachelor.shortestpathmap.api;

import edu.princeton.cs.algs4.In;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;
import org.assertj.core.util.Lists;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RouteController {
    DataModel model;
    IShortestPathAlgorithm dijkstra;

    @GetMapping("/bounds")
    public String bounds() {
        return "[1,2,3,4]";
    }

    @GetMapping("/load/json")
    public String loadJson() {
        var pa = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\json\\route_line.geojson";
        var P     = new GraphParser();
        model     = P.parseFromJson(pa);
        return "V:" + model.V() + " E: " + model.E();
    }

    @GetMapping("/load/dimacs/nyc")
    public String loadDimacsNyc() {
        var P     = new GraphParser();
        String path1 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\USA-road-d.NY.co";
        String path2 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\USA-road-d.NY.gr";
        String path3 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\USA-road-t.NY.gr";
        model = P.ParseFromIn(new In(path1), new In(path2), new In(path3));
        return "V:" + model.V() + " E: " + model.E();
    }

    @GetMapping("/load/dimacs/usa")
    public String loadDimacsUsa() {
        var P     = new GraphParser();
        String path1 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\usa full\\USA-road-d.USA.co";
        String path2 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\usa full\\USA-road-d.USA.gr";
        String path3 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\usa full\\USA-road-t.USA.gr";
        model = P.ParseFromIn(new In(path1), new In(path2), new In(path3));
        return "V:" + model.V() + " E: " + model.E();
    }

    @GetMapping("/load/dimacs/fla")
    public String loadDimacsFla() {
        var P     = new GraphParser();
        String path1 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\FLA\\USA-road-d.FLA.co";
        String path2 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\FLA\\USA-road-d.FLA.gr";
        String path3 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\FLA\\USA-road-t.FLA.gr";
        model = P.ParseFromIn(new In(path1), new In(path2), new In(path3));
        return "V:" + model.V() + " E: " + model.E();
    }

    @GetMapping("/vertices")
    public IVertex[] vertices () {
        /*var path    = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\json\\route_line.geojson";

            var parser     = new GraphParser();
            var model      = parser.parseFromJson(path);
        */
        return model.getVertices();
    }

    @GetMapping("/vertices/trimmed")
    public List<float[]> verticesTrimmed () {
        var lst = new ArrayList<float[]>();
        try {
            var vertices = model.getVertices();

            for (var v : vertices) {
                if (v != null) lst.add(new float[]{v.X(), v.Y()});
            }
        } catch (Exception e) {

        }
        return lst;
    }

    @GetMapping("/route/visited")
    public List<float[]> visited () {
        var lst = new ArrayList<float[]>();
        try {
            var visited = dijkstra.getVisited();
            for (var i : visited) {
                if (i == null) continue;
                var v = model.getVertex(i);
                lst.add(new float[]{v.X(), v.Y()});
            }
        } catch (Exception e) {

        }
        return lst;
    }

    @GetMapping("/route")
    public List<float[]> route (@RequestParam(value = "from") int from, @RequestParam(value = "to") int to) {
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);

        query.addCriterion(
                RouteCriteriaEvaluationType.DISTANCE,
                new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
                1f
        );

        dijkstra = new Dijkstra();
        dijkstra.perform(model.generateGraph(), model, query);


        var rs = dijkstra.pathTo(to);

        var first = true;
        IEdge last = null;
        var lst = new ArrayList<float[]>();
        for (var v : rs) {
            if (v == null) continue;

            if(first) {
                lst.add(new float[]{v.from().X(), v.from().Y()});
                first = false;
            }
            lst.add(new float[]{v.to().X(), v.to().Y()});
            last = v;
        }
        if(last != null) lst.add(new float[]{last.from().X(), last.from().Y()});

        return lst;
    }
}
