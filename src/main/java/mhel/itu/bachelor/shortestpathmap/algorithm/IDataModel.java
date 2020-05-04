package mhel.itu.bachelor.shortestpathmap.algorithm;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public interface IDataModel {
    //Basic information
    int V(); // number of vertices in the model
    int E(); // number of edges in the model
    void setV(int v);
    void setE(int e);

    //Vertices
    IVertex[] getVertices();
    void addVertex(int index, double x, double y);
    SimpleVertex getVertex(int index);

    //Edges
    IEdge[] getEdges();
    int addEdge(int v, int w);
    SimpleEdge getEdge(int index);
    Iterable<SimpleEdge> getAdjacent(int vertex);

    //Properties
    Map<Integer, Map<EdgePropKey, EdgePropValue>> getPropertiesMap();
    Map<EdgePropKey, EdgePropValue> getProps(int index);
    void addProperty(int index, Map<EdgePropKey, EdgePropValue> map);
    boolean hasPropType(int index, EdgePropKey type);

    //Distance
    double[] getDistances();
    void addDist(int index, double dist);
    double getDist(int index);

    //Travel time
    int[] getTravelTimes();
    void addTravelTime(int index, int t);
    int getTravelTime(int index);

    //Landmarks
    void generateRandomLandmarks(int count);
    int[] getLandmarks();
    Map<Integer, double[]> getLandmarksTable();

    //Bounds
    double getMaxX();
    double getMinX();
    double getMaxY();
    double getMinY();
}
