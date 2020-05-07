package mhel.itu.bachelor.shortestpathmap.model;

import edu.princeton.cs.algs4.Bag;

import java.io.Serializable;
import java.util.*;

public class DataModel implements IDataModel, Serializable {
    //Size
    private int V;                                // number of vertices in this digraph
    private int E;                                // number of edges in this digraph

    //Graph
    private IVertex[] vertices;                   // array of size V, each index pointing to a unique vertex
    private IEdge[] edges;                        // array of size E, each index pointing to a unique edge
    private Bag<IEdge>[] adj;                     // array of size E, each index pointing to a unique bag<IEdge>

    //Weight tables
    private double[] distanceTable;               // array of size E, each index pointing a unique edge distance
    private double[] timeTable;                   // array of size E, each index pointing a unique edge travel time

    //Landmark weight tables
    private Map<Integer, HashMap<Integer, Double>> landmarksDistanceTable;
    private Map<Integer, HashMap<Integer, Double>> landmarksTimeTable;

    //Edge Properties
    private Map<Integer, Set<EdgeProperty>> edgePropertyTable;
    private Map<String, Set<String>> propertyMap;

    //Bounds
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minX = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;

    private int edgeIndexCounter;

    public DataModel(int V, int E) {
        this.V                      = V;
        this.E                      = E;
        this.vertices               = new SimpleVertex[V];
        this.edges                  = new SimpleEdge[E];
        this.distanceTable          = new double[E];
        this.timeTable              = new double[E];
        this.adj                    = (Bag<IEdge>[]) new Bag[E];
        this.edgePropertyTable      = new HashMap<>();
        this.landmarksDistanceTable = new HashMap<>();
        this.landmarksTimeTable     = new HashMap<>();
        this.propertyMap            = new HashMap<>();
        edgeIndexCounter            = 0;

        for (int i = 0; i < E; i++) {
            adj[i] = new Bag<>();
        }
    }

    @Override
    public void addVertex(int index, double x, double y) {
        if(index > vertices.length) throw new IllegalArgumentException("Index "+index+"is out of bounds " + vertices.length);
        if(x > maxX) maxX = x;
        if(x < minX) minX = x;
        if(y > maxY) maxY = y;
        if(y < minY) minY = y;
        vertices[index] = new SimpleVertex(index, x, y);
    }

    @Override
    public IVertex getVertex(int index) {
        if(index > vertices.length) throw new IllegalArgumentException("Index "+index+"is out of bounds " + vertices.length);
        return vertices[index];
    }

    @Override
    public int addEdge(int v, int w) {
        for(var e : getAdjacent(v)) if(e.to().I() == w) throw new IllegalArgumentException("Edge collision " + v + " " + w);
        var index = edgeIndexCounter++;
        edges[index] = new SimpleEdge(index, getVertex(v), getVertex(w));
        adj[v].add(edges[index]);
        return index;
    }

    @Override
    public IEdge getEdge(int index) {
        if(index > edges.length) throw new IllegalArgumentException("Index is out of bounds");
        return edges[index];
    }

    @Override
    public Iterable<IEdge> getAdjacent(int vertex) {
        return adj[vertex];
    }

    @Override
    public Bag<IEdge>[] getAdjacencyTable() {
        return adj;
    }

    @Override
    public IEdge[] getEdges() {
        return edges;
    }

    @Override
    public double[] getDistanceTable() {
        return distanceTable;
    }

    @Override
    public double[] getTimeTable() {
        return timeTable;
    }

    @Override
    public void addDist(int index, double dist) {
        distanceTable[index] = dist;
    }

    @Override
    public double getDist(int index) {
        if(index > distanceTable.length) throw new IllegalArgumentException("Index is out of bounds");
        return distanceTable[index];
    }

    @Override
    public void addTravelTime(int index, int t) {
        timeTable[index] = t;
    }

    @Override
    public double getTravelTime(int index) {
        if(index > timeTable.length) throw new IllegalArgumentException("Index is out of bounds");
        return timeTable[index];
    }

    @Override
    public void setLandmarksDistanceTable(Map<Integer, HashMap<Integer, Double>> landmarks) {
        landmarksDistanceTable = landmarks;
    }

    @Override
    public Map<Integer, HashMap<Integer, Double>> getLandmarksDistanceTable() {
        return landmarksDistanceTable;
    }

    @Override
    public void setLandmarksTimeTable(Map<Integer, HashMap<Integer, Double>> landmarks) {
        landmarksDistanceTable = landmarks;
    }

    @Override
    public Map<Integer, HashMap<Integer, Double>> getLandmarksTimeTable() {
        return landmarksTimeTable;
    }

    @Override
    public void addEdgeProperty(int index, EdgeProperty property) {
        var rs = edgePropertyTable.get(index);
        if(rs == null) rs = new HashSet<>();
        rs.add(property);
        edgePropertyTable.put(index, rs);

        addEdgePropertyToMap(property);
    }

    public EdgeProperty getEdgeProperty(int index, String key) {
        for(var p : edgePropertyTable.get(index)) if(p.getKey().equals(key)) return p;
        return null;
    }

    @Override
    public boolean hasEdgeProperty(int index, EdgeProperty prop) {
        var rs = edgePropertyTable.get(index);

        for(var p : rs) {
            if(p.getKey().equals(prop.getKey())) {
                if(p.getValue().equals(prop.getValue())) {
                    return true;
                }
            }
        }
        return rs.contains(prop);
    }

    @Override
    public Set<EdgeProperty> getEdgeProperties(int index) {
        return edgePropertyTable.get(index);
    }

    @Override
    public Map<Integer, Set<EdgeProperty>> getEdgePropertyTable() {
        return edgePropertyTable;
    }

    @Override
    public int V() {
        return V;
    }

    @Override
    public int E() {
        return E;
    }

    @Override
    public void setV(int v) {
        this.V = v;
    }

    @Override
    public void setE(int e) {
        this.E = e;
    }

    @Override
    public IVertex[] getVertices() {
        return vertices;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }
    @Override
    public double getMinX() {
        return minX;
    }
    @Override
    public double getMaxY() {
        return maxY;
    }
    @Override
    public double getMinY() {
        return minY;
    }

    ////////////////////////////////
    //Helper functions
    ////////////////////////////////
    @Override
    public IGraph generateGraph() {
        return new SimpleGraph(this);
    }

    @Override
    public int[] generateRandomLandmarks(int count) {
        var tmp = new int[count];
        var rnd = new Random();
        for(var i = 0; i < count; i++) {
            tmp[i] = rnd.nextInt(V);
        }
        return tmp;
    }

    public void addEdgePropertyToMap(EdgeProperty property) {
        var rs = propertyMap.get(property.getKey());
        if(rs == null) rs = new HashSet<>();
        rs.add(property.getValue());
        propertyMap.put(property.getKey(), rs);
    }
    @Override
    public Map<String, Set<String>> getPropertyMap() {
        return propertyMap;
    }
}
