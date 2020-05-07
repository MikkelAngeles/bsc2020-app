package mhel.itu.bachelor.shortestpathmap.model;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.princeton.cs.algs4.Bag;
import java.util.*;

public interface IDataModel {
    //Basic information
    int V(); // number of vertices in the model
    int E(); // number of edges in the model
    void setV(int v);
    void setE(int e);

    //Vertices
    IVertex[] getVertices();
    void addVertex(int index, double x, double y);
    IVertex getVertex(int index);

    //Edges
    IEdge[] getEdges();
    int addEdge(int v, int w);
    IEdge getEdge(int index);
    Iterable<IEdge> getAdjacent(int vertex);
    Bag<IEdge>[] getAdjacencyTable();

    //Properties
    EdgeProperty getEdgeProperty(int index, String key);
    void addEdgeProperty(int index, EdgeProperty property);
    boolean hasEdgeProperty(int index, EdgeProperty prop);
    Set<EdgeProperty> getEdgeProperties(int index);
    Map<Integer, Set<EdgeProperty>> getEdgePropertyTable();
    Map<String, Set<String>> getPropertyMap();

    //Distance
    double[] getDistanceTable();
    void addDist(int index, double dist);
    double getDist(int index);

    //Travel time
    double[] getTimeTable();
    void addTravelTime(int index, int t);
    double getTravelTime(int index);

    //Landmarks
    Map<Integer, HashMap<Integer, Double>> getLandmarksDistanceTable();
    Map<Integer, HashMap<Integer, Double>> getLandmarksTimeTable();
    void setLandmarksDistanceTable(Map<Integer, HashMap<Integer, Double>> landmarks);
    void setLandmarksTimeTable(Map<Integer, HashMap<Integer, Double>> landmarks);

    //Bounds
    double getMaxX();
    double getMinX();
    double getMaxY();
    double getMinY();

    //Helper functions
    IGraph generateGraph();
    int[] generateRandomLandmarks(int count);
}
