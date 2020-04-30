package mhel.itu.bachelor.shortestpathmap.api;

public class EdgeDTO {
    public float lon1;
    public float lat1;
    public float lon2;
    public float lat2;

    public EdgeDTO(float lon1, float lat1, float lon2, float lat2) {
        this.lon1 = lon1;
        this.lat1 = lat1;
        this.lon2 = lon2;
        this.lat2 = lat2;
    }
}
