package mhel.itu.bachelor.shortestpathmap.algorithm;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.princeton.cs.algs4.In;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

public class GraphParser {
    //String path = "C:\\Users\\mh89\\Documents Offline\\ITU\\bsc2020\\resources\\json\\route_line.geojson";
    public ParsedGraph pg;

    public GraphParser() {}

    public DataModel ParseFromIn(In vertices, In edges, In travelTime) {
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

        vertices.readLine();
        vertices.readLine();
        edges.readLine();
        edges.readLine();

        var dm = new DataModel(V, E);

        for (int i = 0; i < V; i++) {
            vertices.readString();
            int index = vertices.readInt();

            //Ghetto conversion.
            var xInt = vertices.readInt();
            boolean isNegative = xInt < 0;

            var xStr = ""+Math.abs(xInt);
            var substr1 = xStr.substring(0, 2);
            var substr2 = xStr.substring(2);
            var x = Float.parseFloat((isNegative ? "-" : "")+substr1 + "." + substr2);

            var yInt = vertices.readInt();
            isNegative = yInt < 0;
            var yStr = ""+Math.abs(yInt);
            substr1 = yStr.substring(0, 2);
            substr2 = yStr.substring(2);
            var y = Float.parseFloat((isNegative ? "-" : "")+substr1 + "." + substr2);

            dm.addVertex(index, x, y);
        }

        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");

        for (int i = 0; i < E; i++) {
            edges.readString();

            var v = edges.readInt();
            var w = edges.readInt();
            var dist = edges.readInt();

            var e = dm.addEdge(v, w);
            dm.addDist(e, dist);
        }
        return dm;
    }

    public GraphParser(In vertices, In edges) {

        Point2D[] tmpVertices = null;
        int[] tmpEdges = null;

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

        vertices.readLine();
        vertices.readLine();
        edges.readLine();
        edges.readLine();

        var dm = new DataModel(V, E);

        pg = new ParsedGraph();
        tmpVertices = new Point2D[V + 1];

        for (int i = 0; i < V; i++) {
            vertices.readString();
            int index = vertices.readInt();

            //Ghetto conversion.
            var xInt = vertices.readInt();
            boolean isNegative = xInt < 0;

            var xStr = ""+Math.abs(xInt);
            var substr1 = xStr.substring(0, 2);
            var substr2 = xStr.substring(2);
            var x = Float.parseFloat((isNegative ? "" : "")+substr1 + "." + substr2);

            var yInt = vertices.readInt();
            isNegative = yInt < 0;
            var yStr = ""+Math.abs(yInt);
            substr1 = yStr.substring(0, 2);
            substr2 = yStr.substring(2);
            var y = Float.parseFloat((isNegative ? "" : "")+substr1 + "." + substr2);

            var pt = new Point2D.Float(x , y);
            tmpVertices[index] = pt;
            pg.map.add(pt);
            dm.addVertex(index, x, y);

            if(x > pg.max_x) pg.max_x = x;
            if(x < pg.min_x) pg.min_x = x;
            if(y > pg.max_y) pg.max_y = y;
            if(y < pg.min_y) pg.min_y = y;
        }

        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");

        for (int i = 0; i < E; i++) {
            edges.readString();

            var v = edges.readInt();
            var w = edges.readInt();
            var e = dm.addEdge(v, w);
            var dist = edges.readInt();
            dm.addDist(e, dist);

            var v1 = tmpVertices[v];
            var w1 = tmpVertices[w];

            var edge = new ParsedEdge(v1, w1, dist, "", i);
            var criterias = new HashSet<RouteCriteriaEvaluationType>();
            //criterias.add(CriteriaType.ROAD);
            //edge.setProps(criterias);
            pg.edges.add(edge);
        }

        var ideasd = 1;

    }

    public DataModel parseFromJson(String path) {
        JsonObject jsonObject = null;

        try (Reader r = new FileReader(path)) {
            jsonObject = new Gson().fromJson(r, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(jsonObject == null) return null;
        var duplicates = new HashMap<Point2D, Integer>();
        pg = new ParsedGraph();

        JsonArray arr = jsonObject.getAsJsonArray("features");
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

                var x = Float.parseFloat(cur.get(0).toString());
                var y = Float.parseFloat(cur.get(1).toString());

                if(x > pg.max_x) pg.max_x = x;
                if(x < pg.min_x) pg.min_x = x;
                if(y > pg.max_y) pg.max_y = y;
                if(y < pg.min_y) pg.min_y = y;

                var to = new Point2D.Float(x, y);

                indexLookup.put(to, vertexIndex);

                var rs = lineLookup.get(i);
                if(rs == null) rs = new Stack<>();

                rs.add(vertexIndex);
                lineLookup.put(i, rs);
                indexLineLookup.put(to, i);

                vertexIndex = indexLookup.size();
                var count = duplicates.get(to);

                duplicates.put(to, count != null ? (count + 1) : 1);
                pg.map.add(to);

                if(from == null) {
                    from = to;
                    continue;
                }
                else {
                    var dist = getDistanceInMetersSimple((float) from.getX(), (float) from.getY(), (float) to.getX(),(float) to.getY());
                    var edgeTo = new ParsedEdge(from, to, dist, ref, i);

                    for(var ps : propSet.entrySet()) {
                        edgeTo.addProperty(ps.getKey(), ps.getValue());
                    }
                    pg.edges.add(edgeTo);

                    var oneWay = propSet.containsKey(EdgePropKey.ONE_WAY) && propSet.get(EdgePropKey.ONE_WAY) == EdgePropValue.TRUE;
                    if(!oneWay) {
                        var edgeFrom = new ParsedEdge(to, from, dist, ref, i);
                        for(var ps : propSet.entrySet()) {
                            edgeFrom.addProperty(ps.getKey(), ps.getValue());
                        }
                        pg.edges.add(edgeFrom);
                    }
                }
                from = to;
            }
        }

        var v = pg.getMap().size();
        var e = pg.getEdges().size();
        var dm = new DataModel(indexLookup.size(), e);

        indexLookup = new HashMap<Point2D, Integer>();

        var i = 0;
        for (var vertex : pg.getMap()) {
            //var index = indexLookup.get(vertex);
            var index = i++;
            dm.addVertex(index, (float) vertex.getX(), (float)  vertex.getY());
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

    //https://github.com/jasonwinn/haversine/blob/master/Haversine.java
    public float calculateDistance(float lat_from,  float lon_from, float lat_to, float lon_to) {
        var EARTH_RADIUS = 6371f; // Approx Earth radius in KM
        var dLat  = (float) Math.toRadians(lat_to - lat_from);
        var dLong = (float) Math.toRadians(lon_to - lon_from);

        lat_from = (float) Math.toRadians(lat_from);
        lat_to   = (float) Math.toRadians(lat_to);

        var a = (float) Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat_from) * Math.cos(lat_to) * Math.pow(Math.sin(dLong / 2), 2);
        var c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        return EARTH_RADIUS * c;
    }

    // Equirectangular approximation (Pythagoras’ theorem) Use if performance is an issue and accuracy less important, for small distances.
    public static float getDistanceInMetersSimple(float lat_from,  float lon_from, float lat_to, float lon_to){
        var EARTH_RADIUS = 6371f; // Approx Earth radius in KM
        double lat1             = toRadians(lat_from);
        double lat2             = toRadians(lat_to);

        double x = toRadians(Math.abs(lon_from - lon_to) * Math.cos((lat1 + lat2) / 2));
        double y = toRadians(Math.abs(lat1 - lat2));
        float distance = new Double(Math.sqrt( x*x + y*y ) * EARTH_RADIUS).floatValue();

        return distance;
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

    public class ParsedEdge {
        public Point2D from;
        public Point2D to;
        public float weight;
        public Map<EdgePropKey, EdgePropValue> props;
        public String ref;
        public int line;

        public ParsedEdge(Point2D from, Point2D to, float weight, String ref, int line) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.props = new HashMap<>();
            this.ref = ref;
            this.line = line;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
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
        float maxLat = Float.NEGATIVE_INFINITY;
        float minLat = Float.POSITIVE_INFINITY;
        float maxLon = Float.NEGATIVE_INFINITY;
        float minLon = Float.POSITIVE_INFINITY;
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

        public float getMaxLat() {
            return maxLat;
        }

        public void setMaxLat(float maxLat) {
            this.maxLat = maxLat;
        }

        public float getMinLat() {
            return minLat;
        }

        public void setMinLat(float minLat) {
            this.minLat = minLat;
        }

        public float getMaxLon() {
            return maxLon;
        }

        public void setMaxLon(float maxLon) {
            this.maxLon = maxLon;
        }

        public float getMinLon() {
            return minLon;
        }

        public void setMinLon(float minLon) {
            this.minLon = minLon;
        }
    }*/
    public class ParsedGraph {
        float max_x = Float.NEGATIVE_INFINITY;
        float min_x = Float.POSITIVE_INFINITY;
        float max_y = Float.NEGATIVE_INFINITY;
        float min_y = Float.POSITIVE_INFINITY;
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

        public float getMax_x() {
            return max_x;
        }

        public void setMax_x(float max_x) {
            this.max_x = max_x;
        }

        public float getMin_x() {
            return min_x;
        }

        public void setMin_x(float min_x) {
            this.min_x = min_x;
        }

        public float getMax_y() {
            return max_y;
        }

        public void setMax_y(float max_y) {
            this.max_y = max_y;
        }

        public float getMin_y() {
            return min_y;
        }

        public void setMin_y(float min_y) {
            this.min_y = min_y;
        }
    }
}
