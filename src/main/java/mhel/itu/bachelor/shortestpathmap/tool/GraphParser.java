package mhel.itu.bachelor.shortestpathmap.tool;
import com.google.gson.*;

import com.google.gson.stream.JsonReader;
import edu.princeton.cs.algs4.In;
import mhel.itu.bachelor.shortestpathmap.algorithm.DistanceOracle;
import mhel.itu.bachelor.shortestpathmap.model.*;
import mhel.itu.bachelor.shortestpathmap.model.EdgeProperty;

import java.awt.geom.Point2D;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphParser {
    public ParsedGraph pg;
    public DistanceOracle distanceOracle;

    public GraphParser() {}

    public GraphParser(DistanceOracle d) {
        this.distanceOracle = d;
    }

    public DataModel parseFromDimacsPath(String path, boolean reversedEdges) {
        var v = "resources/dimacs/"+path+"/vertices.co";
        var d = "resources/dimacs/"+path+"/distance.gr";
        var t = "resources/dimacs/"+path+"/time.gr";
        var dm = parseDimacsFromIn(new In(v), new In(d), new In(t), reversedEdges);

        var landmarksDist = loadLandmarks("resources/dimacs/"+path+"/landmarks/dist");
        dm.setLandmarksDistanceTable(landmarksDist);

        var landmarksTime = loadLandmarks("resources/dimacs/"+path+"/landmarks/time");
        dm.setLandmarksDistanceTable(landmarksTime);
        return dm;
    }

    public DataModel parseDimacsFromIn(In vertices, In edges, In travelTime, boolean reversedEdges) {
        vertices.readLine();
        vertices.readLine();
        vertices.readLine();
        vertices.readLine();

        edges.readLine();
        edges.readLine();
        edges.readLine();
        edges.readLine();

        var str     = vertices.readLine();
        var split   = str.split(" ");
        var V       = Integer.parseInt(split[split.length - 1]);

        var str1     = edges.readLine();
        var split1   = str1.split(" ");
        var E        = Integer.parseInt(split1[split1.length - 1]);
        if (E < 0) throw new IllegalArgumentException("Number of edges must be non negative");

        vertices.readLine();
        vertices.readLine();
        edges.readLine();
        edges.readLine();

        var tmpVertices = new HashMap<Integer, Point2D>();
        var vertexCollisions = new HashMap<Point2D, Integer>();
        for (int i = 0; i < V; i++) {
            vertices.readString();
            int index = vertices.readInt() - 1; //Subtract 1 because dimacs indices starts at 1.

            //Ghetto conversion.
            var xInt = vertices.readInt();
            boolean isNegative = xInt < 0;

            var xStr = ""+Math.abs(xInt);
            var substr1 = xStr.substring(0, 2);
            var substr2 = xStr.substring(2);
            var x = Double.parseDouble((isNegative ? "-" : "")+substr1 + "." + substr2);

            var yInt = vertices.readInt();
            isNegative = yInt < 0;
            var yStr = ""+Math.abs(yInt);
            substr1 = yStr.substring(0, 2);
            substr2 = yStr.substring(2);
            var y = Double.parseDouble((isNegative ? "-" : "")+substr1 + "." + substr2);

            //Detect vertex collisions
            var pt = new Point2D.Double(x ,y);
            var count = vertexCollisions.get(pt);
            count = count == null ? 1 : count + 1;
            if(count > 1) throw new IllegalArgumentException("Collision detected for vertex " + index + ", count: " + count);
            vertexCollisions.put(pt, count);

            tmpVertices.put(index, pt);
        }

        var tmpEdges = new ArrayList<DimacsEdge>();
        var edgeCollisions = new HashMap<Integer, HashSet<Integer>>();
        for (int i = 0; i < E; i++) {
            edges.readString();

            var v = edges.readInt() - 1; //Subtract 1 because dimacs indices starts at 1.
            var w = edges.readInt() - 1; //Subtract 1 because dimacs indices starts at 1.
            var dist = edges.readInt();

            var cur_set = edgeCollisions.get(v);
            if(cur_set == null) cur_set = new HashSet<>();
            if(cur_set.contains(w)) continue;

            tmpEdges.add(new DimacsEdge(v, w, dist));
            cur_set.add(w);
            edgeCollisions.put(v, cur_set);
        }

        var dm = new DataModel(tmpVertices.size(), tmpEdges.size());

        //Cleaned vertices
        for(var v : tmpVertices.entrySet()) {
            var cur = v.getValue();
            dm.addVertex(v.getKey(), cur.getX(), cur.getY());
        }
        //Cleaned edges
        for (var t : tmpEdges) {
            var e = reversedEdges ? dm.addEdge(t.w, t.v) : dm.addEdge(t.v, t.w);
            dm.addDist(e, t.dist);
        }

        return dm;
    }

    public Map<Integer, HashMap<Integer, Double>> loadLandmarks(String path) {

        var files = new ArrayList<String>();
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            files = (ArrayList<String>) walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        var lst = new HashMap<Integer, HashMap<Integer, Double>>();

        //foreach landmark
        for(var p : files) {
            var landmarks = new In(p);
            var id = landmarks.readInt();
            var map = new HashMap<Integer, Double>();
            while (!landmarks.isEmpty()) {
                var v = landmarks.readInt();
                var d = landmarks.readDouble();
                map.put(v, d);
            }
            lst.put(id, map);
        }
        return lst;
    }


    public DataModel parseJsonModelReversedEdges(String path) {
        JsonObject jsonObject = null;

        try (var r = new FileReader(path)) {
            jsonObject = new Gson().fromJson(r, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        var V               = jsonObject.getAsJsonPrimitive("V").getAsInt();
        var E               = jsonObject.getAsJsonPrimitive("E").getAsInt();
        var vertices        = jsonObject.getAsJsonArray("vertices");
        var edges           = jsonObject.getAsJsonArray("edges");

        var dm = new DataModel(V, E);
        var i = 0;
        for (var v : vertices) {
            var cur = v.getAsJsonArray();
            var x = cur.get(0).getAsDouble();
            var y = cur.get(1).getAsDouble();
            dm.addVertex(i++, x, y);
        }

        for (var e : edges) {
            var cur = e.getAsJsonObject();
            var v = cur.getAsJsonPrimitive("v").getAsInt();
            var w = cur.getAsJsonPrimitive("w").getAsInt();
            var d = cur.getAsJsonPrimitive("dist").getAsDouble();
            var newEdge = dm.addEdge(w, v); //Reversed edges.
            dm.addDist(newEdge, d);
        }
        return dm;
    }

    public DataModel parseJsonModel(String path) {
        JsonObject jsonObject = null;

        try (var r = new FileReader("resources/json/"+path+"/"+path+".json")) { jsonObject = new Gson().fromJson(r, JsonObject.class); }
        catch (IOException e) { e.printStackTrace(); }

        var V               = jsonObject.getAsJsonPrimitive("V").getAsInt();
        var E               = jsonObject.getAsJsonPrimitive("E").getAsInt();
        var vertices        = jsonObject.getAsJsonArray("vertices");
        var edges           = jsonObject.getAsJsonArray("edges");

        var dm = new DataModel(V, E);
        var i = 0;
        for (var v : vertices) {
            var cur = v.getAsJsonArray();
            var x = cur.get(0).getAsDouble();
            var y = cur.get(1).getAsDouble();
            dm.addVertex(i++, x, y);
        }

        for (var e : edges) {
            var cur = e.getAsJsonObject();
            var v = cur.getAsJsonPrimitive("v").getAsInt();
            var w = cur.getAsJsonPrimitive("w").getAsInt();
            var d = cur.getAsJsonPrimitive("dist").getAsDouble();
            var newEdge = dm.addEdge(v, w);
            dm.addDist(newEdge, d);
        }

        var landmarksDist = loadLandmarks("resources/json/"+path+"/landmarks/dist");
        dm.setLandmarksDistanceTable(landmarksDist);

        var landmarksTime = loadLandmarks("resources/json/"+path+"/landmarks/time");
        dm.setLandmarksDistanceTable(landmarksTime);
        return dm;
    }

    public DataModel parseGeoJsonToModel(String path) {
        JsonObject jsonObject = null;

        try (var r = new FileReader(path)) { jsonObject = new Gson().fromJson(r, JsonObject.class); }
        catch (IOException e) { e.printStackTrace(); }

        if(jsonObject == null) return null;
        var duplicates = new HashMap<Point2D, HashSet<Point2D>>();
        pg = new ParsedGraph();

        var arr = jsonObject.getAsJsonArray("features");
        var indexLookup = new HashMap<Point2D, Integer>();

        var lineLookup = new HashMap<Integer, Stack<Integer>>();
        var indexLineLookup = new HashMap<Point2D, Integer>();

        var vertexIndex = 0;
        for (int i = 0; i < arr.size(); i++) {
            var geometry    = arr.get(i).getAsJsonObject().get("geometry");
            var coordinates = geometry.getAsJsonObject().getAsJsonArray("coordinates");
            var props       = arr.get(i).getAsJsonObject().get("properties");
            var propSet     = new HashSet<EdgeProperty>();

            var p = props.getAsJsonObject();
            if (!p.isJsonNull()) {
                for(var entry : p.entrySet()) {
                    var key = entry.getKey();
                    var val = entry.getValue();
                    if(val.isJsonNull()) continue;
                    propSet.add(new EdgeProperty(key, val.getAsString()));
                }
            }

            Point2D from = null;
            for (var c : coordinates) {
                var cur = c.getAsJsonArray();

                var x = Double.parseDouble(cur.get(0).toString());
                var y = Double.parseDouble(cur.get(1).toString());

                if(x > pg.max_x) pg.max_x = x;
                if(x < pg.min_x) pg.min_x = x;
                if(y > pg.max_y) pg.max_y = y;
                if(y < pg.min_y) pg.min_y = y;

                var to = new Point2D.Double(x, y);

                indexLookup.put(to, vertexIndex);

                var rs = lineLookup.get(i);
                if(rs == null) rs = new Stack<>();

                rs.add(vertexIndex);
                lineLookup.put(i, rs);
                indexLineLookup.put(to, i);

                vertexIndex = indexLookup.size();

                pg.map.add(to);
                var curr_set = duplicates.get(from);
                var dup = curr_set != null && curr_set.contains(to);
                if(from == null || dup) {
                    from = to;
                    continue;
                }
                else {
                    var dist    = distanceOracle.haversine(from.getX(), from.getY(), to.getX(), to.getY());
                    var time    = distanceOracle.haversine(from.getX(), from.getY(), to.getX(), to.getY());
                    var edgeTo  = new ParsedEdge(from, to, dist, time, propSet);
                    pg.edges.add(edgeTo);

                    //Prevent duplicates.
                    var set = duplicates.get(from);
                    if(set == null) set = new HashSet<>();
                    set.add(to);
                    duplicates.put(from, set);

                    var oneWay = propSet.contains(new EdgeProperty("oneway", ""));

                    if(!oneWay) {
                        var edgeFrom = new ParsedEdge(to, from, dist, time, propSet);
                        pg.edges.add(edgeFrom);

                        //Prevent duplicates.
                        set = duplicates.get(to);
                        if(set == null) set = new HashSet<>();
                        set.add(from);
                        duplicates.put(to, set);
                    }
                }
                from = to;
            }
        }

        var v = pg.getMap().size();
        var e = pg.getEdges().size();
        var dm = new DataModel(indexLookup.size(), e);

        indexLookup = new HashMap<>();

        var i = 0;
        for (var vertex : pg.getMap()) {
            var index = i++;
            dm.addVertex(index, vertex.getX(),  vertex.getY());
            indexLookup.put(vertex, index);
        }

        for (var edge : pg.getEdges()) {
            var from  = indexLookup.get(edge.from);
            var to = indexLookup.get(edge.to);

            var newEdge = dm.addEdge(from, to);
            dm.addDist(newEdge, edge.distance);
            dm.addTravelTime(newEdge, 1);
            for(var prop : edge.getProps()) {
                dm.addEdgeProperty(newEdge, prop);
            }
        }

        return  dm;
    }

    public class DimacsEdge {
        public int v;
        public int w;
        public double dist;

        public DimacsEdge(int v, int w, double dist) {
            this.v = v;
            this.w = w;
            this.dist = dist;
        }
    }

    public class ParsedEdge {
        public Point2D from;
        public Point2D to;
        public double distance;
        public double time;
        public Set<EdgeProperty> props;

        public ParsedEdge(Point2D from, Point2D to, double distance, double time, Set<EdgeProperty> props) {
            this.from = from;
            this.to = to;
            this.distance = distance;
            this.time = time;
            this.props = props;
        }

        public Point2D getFrom() {
            return from;
        }

        public void setFrom(Point2D from) {
            this.from = from;
        }

        public Point2D getTo() {
            return to;
        }

        public void setTo(Point2D to) {
            this.to = to;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getTime() {
            return time;
        }

        public void setTime(double time) {
            this.time = time;
        }

        public Set<EdgeProperty> getProps() {
            return props;
        }

        public void setProps(Set<EdgeProperty> props) {
            this.props = props;
        }
    }

    public class ParsedGraph {
        double max_x = Double.NEGATIVE_INFINITY;
        double min_x = Double.POSITIVE_INFINITY;
        double max_y = Double.NEGATIVE_INFINITY;
        double min_y = Double.POSITIVE_INFINITY;
        Set<Point2D> map = new HashSet<>();
        Set<ParsedEdge> edges = new HashSet<>();
        public Set<ParsedEdge> getEdges() {
            return edges;
        }
        public Set<Point2D> getMap() {
            return map;
        }
        public void setMap(Set<Point2D> map) {
            this.map = map;
        }
    }

    public static DataModel load(String fileName) {
        FileOutputStream fileOutputStream = null;
        try {
            var fileInputStream = new FileInputStream("resources/models/yourfile.txt");
            var objectInputStream = new ObjectInputStream(fileInputStream);
            var p2 = (DataModel) objectInputStream.readObject();
            objectInputStream.close();
            return p2;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(DataModel dm, String fileName) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("resources/models/"+fileName);
            var objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dm);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var p = new GraphParser();
        var rs = p.parseGeoJsonToModel("resources/geojson/hil.geojson");
        //save(rs);

        //var loaded = load();
        System.out.println("kekw");
    }
}
