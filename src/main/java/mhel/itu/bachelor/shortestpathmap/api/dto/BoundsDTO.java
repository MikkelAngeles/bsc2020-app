package mhel.itu.bachelor.shortestpathmap.api.dto;

public class BoundsDTO {
    public double maxX;
    public double minX;
    public double maxY;
    public double minY;

    public BoundsDTO(double maxX, double minX, double maxY, double minY) {
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
    }
}
