package mhel.itu.bachelor.shortestpathmap.api;

import mhel.itu.bachelor.shortestpathmap.algorithm.BoundsDTO;

import java.util.List;

public class GraphDTO {
    public int V;
    public int E;
    public BoundsDTO bounds;
    public List<double[]> vertices;
    public List<double[]> verticesHull;
    public List<double[]> landmarks;
    public List<double[]> landmarksHull;
    public List<EdgeDTO> edges;
}
