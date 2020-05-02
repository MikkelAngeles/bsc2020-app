package mhel.itu.bachelor.shortestpathmap.api;

import edu.princeton.cs.algs4.GrahamScan;
import edu.princeton.cs.algs4.In;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RouteController {
    DataModel model;
    IShortestPathAlgorithm spAlgorithm;
    SimpleGraph G;
    GraphParser P = new GraphParser();
    @GetMapping("/bounds")
    public BoundsDTO bounds() {
        return new BoundsDTO(model.getMaxX(), model.getMinX(), model.getMaxY(), model.getMinY());
    }

    @GetMapping("/load/json")
    public GraphDTO loadJson() {
        var pa = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\json\\route_line.geojson";
        model = P.parseFromJson(pa);
        return getGraph();
    }

    @GetMapping("/load/dimacs/nyc")
    public GraphDTO loadDimacsNyc() {
        String path1 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\USA-road-d.NY.co";
        String path2 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\USA-road-d.NY.gr";
        String path3 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\USA-road-t.NY.gr";
        model = P.ParseFromIn(new In(path1), new In(path2), new In(path3));
        return getGraph();
    }

    @GetMapping("/load/dimacs/usa")
    public GraphDTO loadDimacsUsa() {
        String path1 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\usa full\\USA-road-d.USA.co";
        String path2 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\usa full\\USA-road-d.USA.gr";
        String path3 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\usa full\\USA-road-t.USA.gr";
        model = P.ParseFromIn(new In(path1), new In(path2), new In(path3));
        return getGraph();
    }

    @GetMapping("/load/dimacs/fla")
    public GraphDTO loadDimacsFla() {
        String path1 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\FLA\\USA-road-d.FLA.co";
        String path2 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\FLA\\USA-road-d.FLA.gr";
        String path3 = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\FLA\\USA-road-t.FLA.gr";
        model = P.ParseFromIn(new In(path1), new In(path2), new In(path3));
        return getGraph();
    }

    @GetMapping("/vertices")
    public IVertex[] vertices () {
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
            var visited = spAlgorithm.getVisited();
            for (var i : visited) {
                if (i == null) continue;
                var v = model.getVertex(i);
                lst.add(new float[]{v.X(), v.Y()});
            }
        } catch (Exception e) {

        }
        return lst;
    }

    @GetMapping("/route/dijkstra")
    public RouteResultDTO routeDijkstra (@RequestParam(value = "from") int from, @RequestParam(value = "to") int to) {
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);

        query.addCriterion(
                RouteCriteriaEvaluationType.DISTANCE,
                new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
                1f
        );

        spAlgorithm = new Dijkstra();
        var dto = getRoute(query);
        dto.algorithm = "Dijkstra";
        return dto;
    }

    @GetMapping("/route/astar")
    public RouteResultDTO routeAstar (@RequestParam(value = "from") int from, @RequestParam(value = "to") int to, @RequestParam(value = "heuristic") float h) {
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);

        query.addCriterion(
                RouteCriteriaEvaluationType.DISTANCE,
                new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
                1f
        );

        spAlgorithm = new Astar(h);
        var dto = getRoute(query);
        dto.algorithm = "A*";
        return dto;
    }

    @GetMapping("/route/astar-landmarks")
    public RouteResultDTO routeAstarLandmarks (@RequestParam(value = "from") int from,
                                               @RequestParam(value = "to") int to,
                                               @RequestParam(value = "heuristic") float h) {
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);

        query.addCriterion(
                RouteCriteriaEvaluationType.DISTANCE,
                new EdgePropSet(EdgePropKey.DEFAULT, EdgePropValue.DEFAULT),
                1f
        );

        spAlgorithm = new AstarLandmarks(h);
        var dto = getRoute(query);
        dto.algorithm = "A* Landmarks";
        return dto;
    }


    //
    /*Move to a repository*/
    //
    public GraphDTO getGraph() {
        G = model.generateGraph();
        model.generateRandomLandmarks(16);
        var dto = new GraphDTO();
        dto.E = model.E();
        dto.V = model.V();
        dto.vertices = getVertices();
        dto.verticesHull = calculateConvexHull(dto.vertices);
        dto.landmarks = getLandmarks();
        dto.landmarksHull = calculateConvexHull(dto.landmarks);
        dto.bounds = new BoundsDTO(model.getMaxX(), model.getMinX(), model.getMaxY(), model.getMinY());
        return dto;
    }

    public RouteResultDTO getRoute(RouteQuery q) {
        var dto         = new RouteResultDTO();
        long before     = System.nanoTime();
        spAlgorithm.perform(G, model, q);
        long after      = System.nanoTime();
        dto.elapsed     = after - before;
        dto.hasPath     = spAlgorithm.hasPath(q.getTarget());
        var rs          = spAlgorithm.pathTo(q.getTarget());
        dto.dist        = spAlgorithm.distTo(q.getTarget());

        var first = true;
        IEdge last = null;
        var lst = new ArrayList<double[]>();
        for (var v : rs) {
            if (v == null) continue;
            if(first) {
                lst.add(new double[]{v.from().X(), v.from().Y()});
                first = false;
            }
            lst.add(new double[]{v.to().X(), v.to().Y()});
            last = v;
        }
        if(last != null) lst.add(new double[]{last.from().X(), last.from().Y()});
        dto.route = lst;
        dto.visited = getVisited();
        dto.hull = calculateConvexHull(verticesQueueToDoubleArr(spAlgorithm.getVisited()));

        return dto;
    }

    public List<double[]> getVisited() {
        var lst = new ArrayList<double[]>();
        var visited = spAlgorithm.getVisited();
        for (var i : visited) {
            if (i == null) continue;
            var v = model.getVertex(i);
            lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<double[]> getLandmarks() {
        var lst = new ArrayList<double[]>();
        for (var i : model.getLandmarks()) {
            var v = model.getVertex(i);
            lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }
    public List<double[]> getVertices() {
        var lst = new ArrayList<double[]>();
        for (var v : model.getVertices()) {
            if (v != null) lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<double[]> verticesQueueToDoubleArr(Queue<Integer> queue) {
        var lst = new ArrayList<double[]>();
        for (var i : queue) {
            var v = model.getVertex(i);
            lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<double[]> verticesListToDoubleArr(List<Integer> arr) {
        var lst = new ArrayList<double[]>();
        for (var i : arr) {
            var v = model.getVertex(i);
            lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<double[]> calculateConvexHull(List<double[]> arr) {
        var points = new edu.princeton.cs.algs4.Point2D[arr.size()];
        var i = 0;

        for(var pt : arr) points[i++] = new edu.princeton.cs.algs4.Point2D(pt[0], pt[1]);

        var gs = new GrahamScan(points);
        var lst = new ArrayList<double[]>();
        for (var v : gs.hull()) {
            if (v != null) lst.add(new double[]{v.x(), v.y()});
        }
        return lst;
    }
}
