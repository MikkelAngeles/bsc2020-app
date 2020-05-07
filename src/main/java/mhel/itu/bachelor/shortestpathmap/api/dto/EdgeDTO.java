package mhel.itu.bachelor.shortestpathmap.api.dto;

public class EdgeDTO {
    public int index;
    public int v;
    public int w;
    public double dist;

    public EdgeDTO(int index, int v, int w, double dist) {
        this.index = index;
        this.v = v;
        this.w = w;
        this.dist = dist;
    }
}
