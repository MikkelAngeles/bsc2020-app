package mhel.itu.bachelor.shortestpathmap.tool;
import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import mhel.itu.bachelor.shortestpathmap.algorithm.DistanceOracle;
import mhel.itu.bachelor.shortestpathmap.api.dto.ModelInfoDTO;
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

import static mhel.itu.bachelor.shortestpathmap.tool.Experiments.timeStamp;

public class GraphParser {
    public ParsedGraph pg;

    public GraphParser() {}

    public DataModel parseFromDimacsPath(String path, boolean reversedEdges, boolean skipLandmarks) {
        var v = new In("resources/dimacs/"+path+"/vertices.co");
        var d = new In("resources/dimacs/"+path+"/distance.gr");
        var t = new In("resources/dimacs/"+path+"/time.gr");
        var dm = parseDimacsFromIn(v,d,t, reversedEdges);
        v.close();
        d.close();
        t.close();

        if(skipLandmarks) return dm;
        var landmarksDist = loadLandmarks("resources/dimacs/"+path+"/landmarks/dist");
        dm.setLandmarksDistanceTable(landmarksDist);

        var landmarksTime = loadLandmarks("resources/dimacs/"+path+"/landmarks/time");
        dm.setLandmarksTimeTable(landmarksTime);
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

        travelTime.readLine();
        travelTime.readLine();
        travelTime.readLine();
        travelTime.readLine();
        travelTime.readLine();
        travelTime.readLine();
        travelTime.readLine();

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

        var tmpEdgeTime = new ArrayList<DimacsEdge>();
        edgeCollisions = new HashMap<>();
        for (int i = 0; i < E; i++) {
            travelTime.readString();
            var v = travelTime.readInt() - 1; //Subtract 1 because dimacs indices starts at 1.
            var w = travelTime.readInt() - 1; //Subtract 1 because dimacs indices starts at 1.
            var dist = travelTime.readInt();

            var cur_set = edgeCollisions.get(v);
            if(cur_set == null) cur_set = new HashSet<>();
            if(cur_set.contains(w)) continue;

            tmpEdgeTime.add(new DimacsEdge(v, w, dist));
            cur_set.add(w);
            edgeCollisions.put(v, cur_set);
        }

        //Cleaned vertices
        for(var v : tmpVertices.entrySet()) {
            var cur = v.getValue();
            dm.addVertex(v.getKey(), cur.getX(), cur.getY());
        }
        //Cleaned edges
        var i = 0;
        for (var t : tmpEdges) {
            var e = reversedEdges ? dm.addEdge(t.w, t.v) : dm.addEdge(t.v, t.w);
            dm.addDist(e, t.dist);
            dm.addTravelTime(e, tmpEdgeTime.get(i++).dist);  //Strictly assuming that the order of the files is correct, otherwise this will break everything.
        }

        return dm;
    }

    public static Map<Integer, HashMap<Integer, Double>> loadLandmarks(String path) {

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
            landmarks.close();
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
        dm.setLandmarksTimeTable(landmarksTime);
        return dm;
    }

    public DataModel parseGeoJsonToModel(String path, boolean reverseEdges) {
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
                    var dist    = DistanceOracle.haversine(from.getX(), from.getY(), to.getX(), to.getY());
                    var edgeTo  = reverseEdges ? new ParsedEdge(to, from, dist, propSet) : new ParsedEdge(from, to, dist, propSet);

                    pg.edges.add(edgeTo);

                    //Prevent duplicates.
                    var set = duplicates.get(from);
                    if(set == null) set = new HashSet<>();
                    set.add(to);
                    duplicates.put(from, set);

                    var oneWay = propSet.contains(new EdgeProperty("oneway", ""));

                    if(!oneWay) {
                        var edgeFrom = reverseEdges ? new ParsedEdge(from, to, dist, propSet) : new ParsedEdge(to, from, dist, propSet);
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

            var speed = 50; //km/t - default speed limit in denmark.
            for(var prop : edge.getProps()) {
                dm.addEdgeProperty(newEdge, prop);
                if(prop.getKey().equals("maxspeed")) speed = Integer.parseInt(prop.getValue());
            }
            dm.addTravelTime(newEdge, DistanceOracle.distSpeedToTime(edge.distance, speed));
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
        public Set<EdgeProperty> props;

        public ParsedEdge(Point2D from, Point2D to, double distance, Set<EdgeProperty> props) {
            this.from = from;
            this.to = to;
            this.distance = distance;
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

    //Deserializes the file from "resources/models/"+fileName" to a DataModel
    public static DataModel load(String fileName) {
        System.out.println(timeStamp() + "loading model from " + fileName);
        long start = System.currentTimeMillis();
        try {
            var fileInputStream = new FileInputStream("resources/models/"+fileName);
            var bufferedInputStream = new BufferedInputStream(fileInputStream);
            var objectInputStream = new ObjectInputStream(bufferedInputStream);
            var p2 = (DataModel) objectInputStream.readObject();
            objectInputStream.close();
            long end = System.currentTimeMillis();
            System.out.println(timeStamp() + "Done loading in " + (end-start) + "ms");
            return p2;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(timeStamp());
            e.printStackTrace();
        }
        return null;
    }

    //Serializes the DataModel in resources/models/fileName
    public static void save(DataModel dm, String fileName, String modelName, int landmarks, String fileOrigin) {
        System.out.println(timeStamp() + "saving model to " + fileName);
        long start = System.currentTimeMillis();
        try {

            var fileOutputStream = new FileOutputStream("resources/models/"+fileName+".model");
            var bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            var objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(dm);
            objectOutputStream.flush();
            objectOutputStream.close();


            var dto = new ModelInfoDTO();
            dto.modelName = modelName;
            dto.fileName = fileName+".model";
            dto.landmarks = landmarks;
            dto.fileOrigin = fileOrigin;
            dto.V = dm.V();
            dto.E = dm.E();

            fileOutputStream = new FileOutputStream("resources/models/"+fileName+".info");
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(dto);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println(timeStamp() + "Done saving in " + (end-start) + "ms");
    }

    public static void main(String[] args) {
        //load("fla-4-landmarks-random");

        System.out.println(timeStamp() + "parsing started");
        long graphStart = System.currentTimeMillis();
        var P   = new GraphParser();
        var M   = P.parseFromDimacsPath("fla",false, false);
        //var M = P.parseGeoJsonToModel("resources/geojson/hil/hil.geojson", false);

        long graphEnd = System.currentTimeMillis();
        System.out.println(timeStamp() + "Done parsing in " + (graphEnd - graphStart)+"ms");

        //fla
        //save(M, "fla-0", "Florida", 0, "dimacs");

        //fla
        save(M, "fla-23", "Florida", 23, "dimacs");

        //Hil-0
        //save(M, "hil-0", "Hillerød", 0, "OSM");

        //Hil-16-good
        //M.setLandmarksDistanceTable(loadLandmarks("resources/geojson/hil/landmarks/dist"));
        //save(M, "hil-16-good", "Hillerød", 16, "OSM");

        //Hil-64
        //M.setLandmarksDistanceTable(loadLandmarks("resources/geojson/hil/landmarks/dist"));
        //save(M, "hil-64", "Hillerød", 64, "OSM");
    }


    ////////////////////////
    //Deprecated tools
    ///////////////////////
    public static void exportDataModelToJson(DataModel m, String modelName) throws IOException {
        var gson        = new GsonBuilder().setPrettyPrinting().create();
        var fw          = new FileWriter("resources/json/"+modelName+"/model.json");
        gson.toJson(m, fw);
        fw.close();
    }

    public static DataModel importModelFromJson(String modelName) throws FileNotFoundException {
        System.out.println(timeStamp() + "importing model from /" + modelName + "/model.json");
        long start = System.currentTimeMillis();
        var gson = new Gson();

        BufferedReader reader = null;
        try {
            var fileInputStream = new FileInputStream("resources/json/"+modelName+"/model.json");
            var bufferedInputStream = new BufferedInputStream(fileInputStream);
            reader = new BufferedReader(new InputStreamReader(bufferedInputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        var model           = gson.fromJson(reader, JsonObject.class);
        var V               = model.getAsJsonPrimitive("V").getAsInt();
        var E               = model.getAsJsonPrimitive("E").getAsInt();
        var vertices        = gson.fromJson(model.getAsJsonArray("vertices"), SimpleVertex[].class);
        var edges           = model.getAsJsonArray("edges");
        var adj             = (Bag<IEdge>[]) new Bag[E];
        for (int i = 0; i < E; i++) adj[i] = new Bag<>();

        var parsedEdges = new SimpleEdge[edges.size()];

        for (var e : edges) {
            var cur = e.getAsJsonObject();
            var i   = cur.getAsJsonPrimitive("index").getAsInt();
            var v   = gson.fromJson(e.getAsJsonObject().get("from"), SimpleVertex.class);
            var w   = gson.fromJson(e.getAsJsonObject().get("to"), SimpleVertex.class);
            parsedEdges[i] = new SimpleEdge(i, v, w);
            adj[v.i].add(parsedEdges[i]);
        }

        var dist            = gson.fromJson(model.getAsJsonArray("distanceTable"), double[].class);
        var time            = gson.fromJson(model.getAsJsonArray("timeTable"), double[].class);

        var type = new TypeToken<Map<Integer, HashMap<Integer, Double>>>(){}.getType();
        Map<Integer, HashMap<Integer, Double>> landmarksDistanceTable = gson.fromJson(model.getAsJsonObject("landmarksDistanceTable"), type);

        type = new TypeToken<Map<Integer, HashMap<Integer, Double>>>(){}.getType();
        Map<Integer, HashMap<Integer, Double>> landmarksTimeTable = gson.fromJson(model.getAsJsonObject("landmarksTimeTable"), type);

        type = new TypeToken<Map<Integer, Set<EdgeProperty>>>(){}.getType();
        Map<Integer, Set<EdgeProperty>> edgePropertyTable   = gson.fromJson(model.getAsJsonObject("edgePropertyTable"), type);

        type = new TypeToken<Map<String, Set<String>>>(){}.getType();
        Map<String, Set<String>> propertyMap = gson.fromJson(model.getAsJsonObject("propertyMap"), type);

        var maxX               = model.getAsJsonPrimitive("maxX").getAsDouble();
        var minX               = model.getAsJsonPrimitive("minX").getAsDouble();
        var maxY               = model.getAsJsonPrimitive("maxY").getAsDouble();
        var minY               = model.getAsJsonPrimitive("minY").getAsDouble();
        var counter            = model.getAsJsonPrimitive("edgeIndexCounter").getAsInt();

        var m = new DataModel(
                V,
                E,
                vertices,
                parsedEdges,
                adj,
                dist,
                time,
                landmarksDistanceTable,
                landmarksTimeTable,
                edgePropertyTable,
                propertyMap,
                maxX,
                minX,
                maxY,
                minY,
                counter
        );

        long end = System.currentTimeMillis();
        System.out.println(timeStamp() + "Done importing in " + (end-start) + "ms");

        return m;
    }

}
