package mhel.itu.bachelor.shortestpathmap.algorithm;

import edu.princeton.cs.algs4.Bag;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class DataModel implements IDataModel {
    private int V;                                // number of vertices in this digraph
    private int E;                                // number of edges in this digraph

    private SimpleVertex[] vertices;
    private SimpleEdge[] edges;
    private Bag<SimpleEdge>[] adj;

    private int edgesCounter;
    private float[] distances;
    private int[] travelTimes;
    private Map<Integer, Map<EdgePropKey, EdgePropValue>> propMap;
    private Map<String, List<Integer>> refMap;
    private Map<Integer, Stack<Integer>> lineMap;

    //Bounds
    private float maxX = Float.NEGATIVE_INFINITY;
    private float minX = Float.POSITIVE_INFINITY;
    private float maxY = Float.NEGATIVE_INFINITY;
    private float minY = Float.POSITIVE_INFINITY;

    //Graphical performance data structure
    private KdTree tree;
    private KdTree vertexTree;

    public DataModel(int V, int E) {
        this.V = V;
        this.E = E;
        this.vertices = new SimpleVertex[V + 1];
        this.edges = new SimpleEdge[E + 1];
        this.distances = new float[E + 1];
        this.travelTimes = new int[E + 1];
        this.adj = (Bag<SimpleEdge>[]) new Bag[E + 1];
        this.propMap = new HashMap<>();
        this.refMap = new HashMap<>();
        this.lineMap = new HashMap<>();
        edgesCounter = 0;

        for (int i = 0; i < E; i++) {
            adj[i] = new Bag<>();
        }
    }

    @Override
    public void addVertex(int index, float x, float y) {
        if(index > vertices.length) throw new IllegalArgumentException("Index is out of bounds");
        if(x > maxX) maxX = x;
        if(x < minX) minX = x;
        if(y > maxY) maxY = y;
        if(y < minY) minY = y;
        vertices[index] = new SimpleVertex(index, x, y);
    }

    @Override
    public SimpleVertex getVertex(int index) {
        if(index > vertices.length) throw new IllegalArgumentException("Index is out of bounds");
        return vertices[index];
    }

    @Override
    public int addEdge(int v, int w) {
        var index = edgesCounter++;
        edges[index] = new SimpleEdge(index, getVertex(v), getVertex(w));
        adj[v].add(edges[index]);
        return index;
    }

    public void addEdgeRef(String ref, int edge) {
        var rs = refMap.get(ref);
        if(rs == null) rs = new ArrayList<>();
        rs.add(edge);
        refMap.put(ref, rs);
    }

    public void addVertexToLine(int line, int vertex) {
        var rs = lineMap.get(line);
        if(rs == null) rs = new Stack<>();
        if(rs.contains(vertex)) return;

        rs.add(vertex);
        lineMap.put(line, rs);
    }

    @Override
    public SimpleEdge getEdge(int index) {
        if(index > edges.length) throw new IllegalArgumentException("Index is out of bounds");
        return edges[index];
    }

    @Override
    public Iterable<SimpleEdge> getAdjacent(int vertex) {
        return adj[vertex];
    }

    @Override
    public IEdge[] getEdges() {
        return edges;
    }

    @Override
    public float[] getDistances() {
        return distances;
    }

    @Override
    public int[] getTravelTimes() {
        return travelTimes;
    }

    @Override
    public Map<Integer, Map<EdgePropKey, EdgePropValue>> getPropertiesMap() {
        return propMap;
    }

    @Override
    public void addDist(int index, float dist) {
        distances[index] = dist;
    }
    @Override
    public float getDist(int index) {
        if(index > distances.length) throw new IllegalArgumentException("Index is out of bounds");
        return distances[index];
    }
    @Override
    public void addTravelTime(int index, int t) {
        travelTimes[index] = t;
    }
    @Override
    public int getTravelTime(int index) {
        if(index > travelTimes.length) throw new IllegalArgumentException("Index is out of bounds");
        return travelTimes[index];
    }

    @Override
    public void addProperty(int index, Map<EdgePropKey, EdgePropValue> map) {
        propMap.put(index, map);
    }

    @Override
    public Map<EdgePropKey, EdgePropValue> getProps(int index) {
        return propMap.get(index);
    }

    @Override
    public boolean hasPropType(int index, EdgePropKey type) {
        if(type == EdgePropKey.DEFAULT) return true;
        var rs = propMap.get(index);
        return rs.containsKey(type);
    }

    public EdgePropValue getProp(int index, EdgePropKey type) {
        if(!hasPropType(index, type)) return null;
        return propMap.get(index).get(type);
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
    public float getMaxX() {
        return maxX;
    }
    @Override
    public float getMinX() {
        return minX;
    }
    @Override
    public float getMaxY() {
        return maxY;
    }
    @Override
    public float getMinY() {
        return minY;
    }

    ////////////////////////////////
    //Helper functions
    ////////////////////////////////

    public SimpleGraph generateGraph() {
        return new SimpleGraph(this);
    }

    public void createKdTree2() {
        var lst = new ArrayList<EnhancedShape>();
        var collisions = 0;
        for (var edge : edges) {
            if(edge == null) continue;

            var path = new Path2D.Float();
            path.moveTo(edge.from().X(), edge.from().Y());
            path.lineTo(edge.to().X(), edge.to().Y());


            //var sh = new Rectangle2D.Float(v.x, v.y, 0.000013f, 0.000013f);
            var esh = new EnhancedShape(path, edge.index());
            lst.add(esh);
        }
        tree = new KdTree(lst);
    }

    public void createKdTree3() {
        var lst = new ArrayList<EnhancedShape>();
        var collisions = 0;
        for (var entry : lineMap.entrySet()) {
            if(entry == null) continue;

            var lines = entry.getValue();
            var path = new Path2D.Float();

            var first = true;
            for(var line : lines) {
                var vertex = getVertex(line);
                if(first) {
                    path.moveTo(vertex.X(), vertex.Y());
                    first = false;
                }
                else path.lineTo(vertex.X(), vertex.Y());
            }

            //var sh = new Rectangle2D.Float(v.x, v.y, 0.000013f, 0.000013f);
            var esh = new EnhancedShape(path, entry.getKey());
            lst.add(esh);
        }
        tree = new KdTree(lst);
    }

    public void createKdTree() {
        createVertexNodes();
        var lst = new ArrayList<EnhancedShape>();
        var collisions = 0;
        for (var edge : edges) {
            if(edge == null) continue;

            var path = new Path2D.Float();
            path.moveTo(edge.from().X(), edge.from().Y());
            path.lineTo(edge.to().X(), edge.to().Y());

            //var sh = new Rectangle2D.Float(v.x, v.y, 0.000013f, 0.000013f);
            var esh = new EnhancedShape(path, edge.index());
            lst.add(esh);
        }
        tree = new KdTree(lst);
    }

    public void createVertexNodes() {
        var lst = new ArrayList<EnhancedShape>();
        var collisions = 0;
        for (var v : vertices) {
            if(v == null) continue;

            var sh = new Rectangle2D.Float(v.x, v.y, 0.000013f, 0.000013f);
            var esh = new EnhancedShape(sh, v.i);
            lst.add(esh);
        }
        vertexTree = new KdTree(lst);
    }

    public KdTree getKdTree() {
        return tree;
    }
    public KdTree getVertexTree() {
        return vertexTree;
    }

    public SimpleVertex getVertexFromPoint(float x, float y) {
        //var rs = Arrays.stream(vertices).filter(e -> (e != null && e.x == x && e.y == y)).findFirst();
        //return rs.orElse(null);
        var s = new Rectangle2D.Float(x, y, 0.000013f, 0.000013f);


        for(var v : vertices) {
            if(v == null) continue;;
            var t = new Rectangle2D.Float(v.x, v.y, 0.000013f, 0.000013f);
            if(s.intersects(t)) return v;
        }
        return null;
    }

}
