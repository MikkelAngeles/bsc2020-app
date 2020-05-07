package mhel.itu.bachelor.shortestpathmap.api.dto;

import java.util.List;

public class RouteResultDTO {
    public String algorithm;
    public boolean hasPath;
    public double dist;
    public long elapsed;
    public List<double[]> route;
    public List<double[]> visited;
    public List<double[]> hull;

    public RouteResultDTO() { }
}
