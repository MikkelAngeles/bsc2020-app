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

    @GetMapping("/load/json/hil")
    public GraphDTO loadJsonHi() {
        var pa = "resources/json/hil/hil.json";
        model = P.parseFromMyJson(pa);
        return getGraph();
    }

    @GetMapping("/load/dimacs/nyc")
    public GraphDTO loadDimacsNyc() {
        String v = "resources/dimacs/nyc/vertices.co";
        String d = "resources/dimacs/nyc/distance.gr";
        String t = "resources/dimacs/nyc/time.gr";
        model = P.ParseFromIn(new In(v), new In(d), new In(t));
        return getGraph();
    }

    @GetMapping("/edges")
    public List<EdgeDTO> vertices (@RequestParam(value = "vertex") int vertex) {
        return getEdgesByVertex(vertex);
    }

    @GetMapping("/vertices")
    public IVertex[] vertices () {
        return model.getVertices();
    }

    @GetMapping("/vertices/trimmed")
    public List<double[]> verticesTrimmed () {
        var lst = new ArrayList<double[]>();
        try {
            var vertices = model.getVertices();

            for (var v : vertices) {
                if (v != null) lst.add(new double[]{v.X(), v.Y()});
            }
        } catch (Exception e) {

        }
        return lst;
    }

    @GetMapping("/route/visited")
    public List<double[]> visited () {
        var lst = new ArrayList<double[]>();
        try {
            var visited = spAlgorithm.getVisited();
            for (var i : visited) {
                if (i == null) continue;
                var v = model.getVertex(i);
                lst.add(new double[]{v.X(), v.Y()});
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
    public RouteResultDTO routeAstar (@RequestParam(value = "from") int from, @RequestParam(value = "to") int to, @RequestParam(value = "heuristic") double h) {
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
                                               @RequestParam(value = "heuristic") double h) {
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
        if(dto.vertices.size() > 0) dto.verticesHull = calculateConvexHull(dto.vertices);

        dto.landmarks = getLandmarks();
        if(dto.landmarks.size() > 0) dto.landmarksHull = calculateConvexHull(dto.landmarks);

        dto.bounds = new BoundsDTO(model.getMaxX(), model.getMinX(), model.getMaxY(), model.getMinY());
        dto.edges = getEdges();
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
        for (var i : model.getLandmarksTable().entrySet()) {
            var v = model.getVertex(i.getKey());
            lst.add(new double[]{v.X(), v.Y()});
        }
/*
        var hull = calculateConvexHull(getVertices());
        var landmarks = new int[16];
        var rnd = new Random();
        for(var i = 0; i < 16; i++) {
            var rs = hull.get(rnd.nextInt(hull.size()));
            lst.add(new double[]{rs[0], rs[1]});
            //landmarks[i] = hull.get(rnd.nextInt(hull.size()));
        }

        *//*model.generateRandomLandmarks(16);
        for (var i : model.getLandmarks()) {
            var v = model.getVertex(i);
            lst.add(new double[]{v.X(), v.Y()});
            break;
        }*/

        return lst;
    }
    public List<double[]> getVertices() {
        var lst = new ArrayList<double[]>();
        for (var v : model.getVertices()) {
            if (v != null) lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<EdgeDTO> getEdgesByVertex(int v) {
        var lst = new ArrayList<EdgeDTO>();
        for (var e : model.getAdjacent(v)) {
            if (e != null) lst.add(new EdgeDTO(e.index(), e.from().I(), e.to().I(), model.getDist(e.index())));
        }
        return lst;
    }

    public List<EdgeDTO> getEdges() {
        var lst = new ArrayList<EdgeDTO>();
        for (var e : model.getEdges()) {
            if (e != null) lst.add(new EdgeDTO(e.index(), e.from().I(), e.to().I(), model.getDist(e.index())));
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
        if(arr.size() == 0) return null;

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
