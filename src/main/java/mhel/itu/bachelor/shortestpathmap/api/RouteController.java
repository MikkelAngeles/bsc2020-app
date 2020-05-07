package mhel.itu.bachelor.shortestpathmap.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.princeton.cs.algs4.GrahamScan;
import mhel.itu.bachelor.shortestpathmap.algorithm.*;
import mhel.itu.bachelor.shortestpathmap.api.dto.*;
import mhel.itu.bachelor.shortestpathmap.model.*;
import mhel.itu.bachelor.shortestpathmap.tool.*;

import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RouteController {
    IDataModel M;
    IShortestPathAlgorithm SP;
    IGraph G;
    DistanceOracle D = new DistanceOracle();
    GraphParser P  = new GraphParser();

    @GetMapping("/bounds")
    public BoundsDTO bounds() {
        return new BoundsDTO(M.getMaxX(), M.getMinX(), M.getMaxY(), M.getMinY());
    }

    @GetMapping("/load/geojson/hil")
    public GraphDTO loadGeoJsonHil() {
        M = P.parseGeoJsonToModel("resources/geojson/hil.geojson");
        D = new DistanceOracle(M);
        return getGraph();
    }

    @GetMapping("/load/json/hil")
    public GraphDTO loadJsonHil() {
        M = P.parseJsonModel("hil");
        D = new DistanceOracle(M);
        return getGraph();
    }

    @GetMapping("/load/dimacs")
    public GraphDTO loadDimacs(@RequestParam(value = "path") String path) {
        M = P.parseFromDimacsPath(path, false);
        D = new DistanceOracle(M);
        return getGraph();
    }

    @GetMapping("/model/properties")
    public Map<String, Set<String>> getPropertyMap() {
        return M.getPropertyMap();
    }

    @GetMapping("/edges")
    public List<EdgeDTO> vertices (@RequestParam(value = "vertex") int vertex) {
        return getEdgesByVertex(vertex);
    }

    @GetMapping("/vertices")
    public IVertex[] vertices () {
        return M.getVertices();
    }

    @GetMapping("/vertices/trimmed")
    public List<double[]> verticesTrimmed () {
        var lst = new ArrayList<double[]>();
        try {
            var vertices = M.getVertices();
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
            var visited = SP.getVisited();
            for (var i : visited) {
                if (i == null) continue;
                var v = M.getVertex(i);
                lst.add(new double[]{v.X(), v.Y()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lst;
    }

    @GetMapping("/route/dijkstra/criteria")
    public RouteResultDTO routeDijkstra (@RequestParam(value = "from") int from,
                                         @RequestParam(value = "to") int to,
                                         @RequestParam(value = "criteria") String criteria) {

        var parsedCriteriaList = new Gson().fromJson(criteria, RouteCriterion[].class);
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);
        query.setCriteria(parsedCriteriaList);

        SP = new Dijkstra();
        var dto = getRoute(query);
        dto.algorithm = "Dijkstra";
        return dto;
    }

    @GetMapping("/route/dijkstra")
    public RouteResultDTO routeDijkstra (@RequestParam(value = "from") int from, @RequestParam(value = "to") int to) {
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);

        try {
            SP = new Dijkstra();
        } catch(Exception e) {
            e.printStackTrace();
        }
        var dto = getRoute(query);
        dto.algorithm = "Dijkstra";
        return dto;
    }

    @GetMapping("/route/astar")
    public RouteResultDTO routeAstar (@RequestParam(value = "from") int from, @RequestParam(value = "to") int to, @RequestParam(value = "heuristic") double h) {
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);

        SP = new Astar(h);
        var dto = getRoute(query);
        dto.algorithm = "A*";
        return dto;
    }

    @GetMapping("/route/astar/criteria")
    public RouteResultDTO routeAstarCriteria (@RequestParam(value = "from") int from,
                                              @RequestParam(value = "to") int to,
                                              @RequestParam(value = "heuristic") double h,
                                              @RequestParam(value = "criteria") String c) {

        var parsedCriteriaList = new Gson().fromJson(c, RouteCriterion[].class);
        var query = new RouteQuery();
        query.setSource(from);
        query.setTarget(to);
        query.setCriteria(parsedCriteriaList);

        SP = new Astar(h);
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

        SP = new AstarLandmarks(h);
        var dto = getRoute(query);
        dto.algorithm = "A* Landmarks";
        return dto;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    /*Route repository - move to it's own class and control it with an injected interface later*/
    /////////////////////////////////////////////////////////////////////////////////////////////
    public GraphDTO getGraph() {
        G = M.generateGraph();
        M.generateRandomLandmarks(16);
        var dto = new GraphDTO();
        dto.E = M.E();
        dto.V = M.V();
        dto.vertices = getVertices();
        if(dto.vertices.size() > 0) dto.verticesHull = calculateConvexHull(dto.vertices);

        dto.landmarks = getLandmarks();
        if(dto.landmarks.size() > 0) dto.landmarksHull = calculateConvexHull(dto.landmarks);

        dto.bounds = new BoundsDTO(M.getMaxX(), M.getMinX(), M.getMaxY(), M.getMinY());
        dto.edges = getEdges();
        return dto;
    }

    public RouteResultDTO getRoute(RouteQuery q) {
        var dto         = new RouteResultDTO();
        long before     = System.nanoTime();
        SP.load(G, D, q);
        long after      = System.nanoTime();
        dto.elapsed     = after - before;
        dto.hasPath     = SP.hasPath(q.getTarget());
        var rs          = SP.pathTo(q.getTarget());
        dto.dist        = SP.distTo(q.getTarget());

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
        dto.hull = calculateConvexHull(verticesQueueToDoubleArr(SP.getVisited()));

        return dto;
    }

    public List<double[]> getVisited() {
        var lst = new ArrayList<double[]>();
        var visited = SP.getVisited();
        for (var i : visited) {
            if (i == null) continue;
            var v = M.getVertex(i);
            lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<double[]> getLandmarks() {
        var lst = new ArrayList<double[]>();
        for (var i : M.getLandmarksDistanceTable().entrySet()) {
            var v = M.getVertex(i.getKey());
            lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }
    public List<double[]> getVertices() {
        var lst = new ArrayList<double[]>();
        for (var v : M.getVertices()) {
            if (v != null) lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<EdgeDTO> getEdgesByVertex(int v) {
        var lst = new ArrayList<EdgeDTO>();
        for (var e : M.getAdjacent(v)) {
            if (e != null) lst.add(new EdgeDTO(e.index(), e.from().I(), e.to().I(), M.getDist(e.index())));
        }
        return lst;
    }

    public List<EdgeDTO> getEdges() {
        var lst = new ArrayList<EdgeDTO>();
        for (var e : M.getEdges()) {
            if (e != null) lst.add(new EdgeDTO(e.index(), e.from().I(), e.to().I(), M.getDist(e.index())));
        }
        return lst;
    }

    public List<double[]> verticesQueueToDoubleArr(Queue<Integer> queue) {
        var lst = new ArrayList<double[]>();
        for (var i : queue) {
            var v = M.getVertex(i);
            lst.add(new double[]{v.X(), v.Y()});
        }
        return lst;
    }

    public List<double[]> verticesListToDoubleArr(List<Integer> arr) {
        var lst = new ArrayList<double[]>();
        for (var i : arr) {
            var v = M.getVertex(i);
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
