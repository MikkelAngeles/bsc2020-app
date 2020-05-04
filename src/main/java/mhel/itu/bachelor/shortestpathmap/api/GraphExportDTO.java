package mhel.itu.bachelor.shortestpathmap.api;

import mhel.itu.bachelor.shortestpathmap.algorithm.BoundsDTO;
import mhel.itu.bachelor.shortestpathmap.algorithm.EdgePropKey;
import mhel.itu.bachelor.shortestpathmap.algorithm.EdgePropValue;

import java.util.List;
import java.util.Map;

public class GraphExportDTO {
    public int V;
    public int E;
    public BoundsDTO bounds;
    public List<double[]> vertices;
    public List<double[]> landmarks;
    public List<EdgeDTO> edges;
    public Map<Integer, Map<EdgePropKey, EdgePropValue>> propMap;
    private double[] distances;
    private double[] travelTimes;
}
