package mhel.itu.bachelor.shortestpathmap.algorithm;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.princeton.cs.algs4.In;

import java.awt.geom.Point2D;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphParser {
    //String path = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\json\\route_line.geojson";
    public ParsedGraph pg;
    public DistanceOracle distanceOracle;

    public GraphParser() {}

    public GraphParser(DistanceOracle d) {
        this.distanceOracle = d;
    }

    public DataModel parseFromDimacsPath(String path, boolean reversedEdges) {
        String v = "resources/dimacs/"+path+"/vertices.co";
        String d = "resources/dimacs/"+path+"/distance.gr";
        String t = "resources/dimacs/"+path+"/time.gr";
        return parseDimacsFromIn(new In(v), new In(d), new In(t), reversedEdges);
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

        var landmarks = loadLandmarks("resources/dimacs/nyc/landmarks/dist");
        dm.addLandmarks(landmarks);

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


    public DataModel parseFromMyJsonReverseEdges(String path) {
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

        //var landmarks = loadLandmarks();
        //dm.addLandmarks(landmarks);
        return dm;
    }

    public DataModel parseFromMyJson(String path) {
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
            var newEdge = dm.addEdge(v, w);
            dm.addDist(newEdge, d);
        }

        var landmarks = loadLandmarks("resources/json/hil/landmarks/dist");
        dm.addLandmarks(landmarks);
        return dm;
    }

    public DataModel parseFromJson(String path) {
        JsonObject jsonObject = null;

        try (var r = new FileReader(path)) {
            jsonObject = new Gson().fromJson(r, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            var propSet     = new HashMap<EdgePropKey, EdgePropValue>();

            var p = props.getAsJsonObject().get("ref");
            var ref = !p.isJsonNull() ? p.toString() : "";

            addProperty(propSet, props, "oneway");
            addProperty(propSet, props, "highway");
            addProperty(propSet, props, "route");
            addProperty(propSet, props, "foot");

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
                    var dist = distanceOracle.haversine(from.getX(), from.getY(), to.getX(), to.getY());
                    var edgeTo = new ParsedEdge(from, to, dist, ref, i);

                    for(var ps : propSet.entrySet()) {
                        edgeTo.addProperty(ps.getKey(), ps.getValue());
                    }
                    pg.edges.add(edgeTo);

                    //Prevent duplicates.
                    var set = duplicates.get(from);
                    if(set == null) set = new HashSet<>();
                    set.add(to);
                    duplicates.put(from, set);

                    var oneWay = propSet.containsKey(EdgePropKey.ONE_WAY) && propSet.get(EdgePropKey.ONE_WAY) == EdgePropValue.TRUE;
                    if(!oneWay) {
                        var edgeFrom = new ParsedEdge(to, from, dist, ref, i);
                        for(var ps : propSet.entrySet()) {
                            edgeFrom.addProperty(ps.getKey(), ps.getValue());
                        }
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
            //var index = indexLookup.get(vertex);
            var index = i++;
            dm.addVertex(index, vertex.getX(),  vertex.getY());
            indexLookup.put(vertex, index);

            //var line = indexLineLookup.get(vertex);
            //dm.addVertexToLine(line, index);
        }

        for (var edge : pg.getEdges()) {
            var from  = indexLookup.get(edge.from);
            var to = indexLookup.get(edge.to);

            var newEdge = dm.addEdge(from, to);
            dm.addDist(newEdge, edge.weight);
            dm.addTravelTime(newEdge, 1);
            dm.addProperty(newEdge, edge.getProps());
            if(edge.getRef() != null) dm.addEdgeRef(edge.getRef(), newEdge); //Used to map edge relations
            //Used to map edges to the related line from the parsed data.
        }

        return  dm;
    }



    public EdgePropKey keyToEdgePropertyType(String key) {
        switch (key) {
            case "ref":
                return EdgePropKey.REF;
            case "highway":
                return EdgePropKey.ROAD_TYPE;
            case "car":
                return EdgePropKey.CAR;
            case "bike":
                return EdgePropKey.BIKE;
            case "foot":
                return EdgePropKey.FOOT;
            case "oneway":
                return EdgePropKey.ONE_WAY;
            case "maxspeed":
                return EdgePropKey.MAX_SPEED;
            case "lanes":
                return EdgePropKey.LANES;
            case "route":
                return EdgePropKey.ROUTE;
            case "surface":
                return EdgePropKey.SURFACE;
            default:
                return EdgePropKey.UNKNOWN;
        }
    }

    public EdgePropValue getEdgeProperty(EdgePropKey type, String key) {
        switch (type) {
            case ROAD_TYPE:
                return EdgePropValue.ROAD;
            case ONE_WAY:
                return key.equals("yes") ? EdgePropValue.TRUE : EdgePropValue.FALSE;
            default:
                return EdgePropValue.UNKNOWN;
        }
    }

    public void addProperty(HashMap<EdgePropKey, EdgePropValue> set, JsonElement props, String key) {
        try {
            var p = props.getAsJsonObject().get(key);
            if (!p.isJsonNull()) {
                var rs = keyToEdgePropertyType(key);
                if(rs == null) return;

                var rs2 = getEdgeProperty(rs, p.toString());
                if(rs2 == null) return;
                set.put(rs, rs2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double toRadians(double coordinateXorY) {
        return coordinateXorY * Math.PI / 180;
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
        public double weight;
        public Map<EdgePropKey, EdgePropValue> props;
        public String ref;
        public int line;

        public ParsedEdge(Point2D from, Point2D to, double weight, String ref, int line) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.props = new HashMap<>();
            this.ref = ref;
            this.line = line;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public Map<EdgePropKey, EdgePropValue> getProps() {
            return props;
        }

        public void addProperty(EdgePropKey ept, EdgePropValue ep) {
            props.put(ept, ep);
        }

        public String getRef() {
            return ref;
        }
    }

    /*public class ParsedGraph {
        double maxLat = double.NEGATIVE_INFINITY;
        double minLat = double.POSITIVE_INFINITY;
        double maxLon = double.NEGATIVE_INFINITY;
        double minLon = double.POSITIVE_INFINITY;
        Set<Point2D> map = new HashSet<>();
        Set<ParsedEdge> edges = new HashSet<>();
        int V;
        int E;

        public Set<ParsedEdge> getEdges() {
            return edges;
        }

        public Set<Point2D> getMap() {
            return map;
        }

        public void setMap(Set<Point2D> map) {
            this.map = map;
        }

        public int getV() {
            return map.size();
        }

        public void setV(int v) {
            V = v;
        }

        public int getE() {
            return edges.size();
        }

        public void setE(int e) {
            E = e;
        }

        public double getMaxLat() {
            return maxLat;
        }

        public void setMaxLat(double maxLat) {
            this.maxLat = maxLat;
        }

        public double getMinLat() {
            return minLat;
        }

        public void setMinLat(double minLat) {
            this.minLat = minLat;
        }

        public double getMaxLon() {
            return maxLon;
        }

        public void setMaxLon(double maxLon) {
            this.maxLon = maxLon;
        }

        public double getMinLon() {
            return minLon;
        }

        public void setMinLon(double minLon) {
            this.minLon = minLon;
        }
    }*/
    public class ParsedGraph {
        double max_x = Double.NEGATIVE_INFINITY;
        double min_x = Double.POSITIVE_INFINITY;
        double max_y = Double.NEGATIVE_INFINITY;
        double min_y = Double.POSITIVE_INFINITY;
        Set<Point2D> map = new HashSet<>();
        Set<ParsedEdge> edges = new HashSet<>();

        int V;
        int E;

        public Set<ParsedEdge> getEdges() {
            return edges;
        }

        public Set<Point2D> getMap() {
            return map;
        }

        public void setMap(Set<Point2D> map) {
            this.map = map;
        }

        public int getV() {
            return map.size();
        }

        public void setV(int v) {
            V = v;
        }

        public int getE() {
            return edges.size();
        }

        public void setE(int e) {
            E = e;
        }

        public double getMax_x() {
            return max_x;
        }

        public void setMax_x(double max_x) {
            this.max_x = max_x;
        }

        public double getMin_x() {
            return min_x;
        }

        public void setMin_x(double min_x) {
            this.min_x = min_x;
        }

        public double getMax_y() {
            return max_y;
        }

        public void setMax_y(double max_y) {
            this.max_y = max_y;
        }

        public double getMin_y() {
            return min_y;
        }

        public void setMin_y(double min_y) {
            this.min_y = min_y;
        }
    }

    public static void main(String[] args) {
        var G = new GraphParser();
        G.parseFromJson("resources/geojson/fyn.geojson");
        System.out.println("");
    }
}
