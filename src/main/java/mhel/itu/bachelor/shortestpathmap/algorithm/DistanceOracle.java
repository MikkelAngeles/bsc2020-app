package mhel.itu.bachelor.shortestpathmap.algorithm;

import java.util.List;

import static java.lang.Double.isNaN;

public class DistanceOracle {
    private IDataModel dataModel;
    private double distUpper = 1;
    private double timeUpper = 1;

    public DistanceOracle() { }

    public DistanceOracle(IDataModel dataModel) {
        this.dataModel = dataModel;

        var dMax = Double.NEGATIVE_INFINITY;
        for(var d : dataModel.getDistances()) if(d > dMax) dMax = d;
        distUpper = dMax;

        var tMax = Double.NEGATIVE_INFINITY;
        for(var t : dataModel.getTravelTimes()) if(t > tMax) tMax = t;
        timeUpper = tMax;
    }

    public double time(int edge) {
        var convertedTime =  getTimeFactor(dataModel.getTravelTime(edge));
        if(convertedTime < 0) throw new IllegalArgumentException("Distance cannot be negative!");
        if(isNaN(convertedTime)) throw new IllegalArgumentException("Distance is not a number!");
        return convertedTime;
    }

    public double getTimeFactor(double t) {
        return (t / timeUpper) / 2;
    }

    public double dist(int edge) {
        var convertedDist =  getDistFactor(dataModel.getDist(edge));
        if(convertedDist < 0) throw new IllegalArgumentException("Distance cannot be negative!");
        if(isNaN(convertedDist)) throw new IllegalArgumentException("Distance is not a number!");
        return convertedDist;
    }

    public double getDistFactor(double d) {
        return (d / distUpper) / 2;
    }

    public double landmarkDist(int n, int t) {
        var landmarks =  dataModel.getLandmarksTable();
        double max = 0;

        if(n == t) return max;
        /*
        if(landmarks.containsKey(n)) {
            max = landmarks.get(n).get(n);
            if(max < 0) throw new IllegalArgumentException("Landmark distance cannot be negative!");
            if(isNaN(max)) throw new IllegalArgumentException("Landmark distance is not a number!");
        }

        if(landmarks.containsKey(t)) {
            max = landmarks.get(t).get(t);
            if(max < 0) throw new IllegalArgumentException("Landmark distance cannot be negative!");
            if(isNaN(max)) throw new IllegalArgumentException("Landmark distance is not a number!");
        }
        */
        for(var l : dataModel.getLandmarksTable().entrySet()) {
            var distTable = l.getValue();

            var val1 = distTable.get(n);
            var val2 = distTable.get(t);
            if(val1 == null || val2 == null) continue;

            if(isNaN(val1) || isNaN(val2)) continue;
            if(val1 == Double.POSITIVE_INFINITY || val2 == Double.POSITIVE_INFINITY) continue;

            double dist = Math.abs(val1 - val2);
            if(isNaN(dist)) continue;
            max = Math.max(max, dist);
        }

        if(max < 0) throw new IllegalArgumentException("Landmark distance cannot be negative!");
        if(isNaN(max)) throw new IllegalArgumentException("Landmark distance is not a number!");
        return max;
    }


    public double accumRealDist(List<Double> list) {
        return list.stream().mapToDouble(e -> (e * 2) * distUpper).sum();
    }

    public double accumRealTime(List<Double> list) {
        return list.stream().mapToDouble(e -> (e * 2) * timeUpper).sum();
    }

    public double manhattan(double x1,  double y1, double x2, double y2) {
        var delta_x = Math.abs(x2 - x1);
        var delta_y = Math.abs(y2 - y1);
        return (delta_x + delta_y);
    }

    public double haversine(int v, int w) {
        var n = dataModel.getVertex(v);
        var t = dataModel.getVertex(w);
        var tmp =  haversine(n.X(), n.Y(), t.X(), t.Y());
        return getDistFactor(tmp);
    }

    //https://github.com/jasonwinn/haversine/blob/master/Haversine.java
    public double haversine(double lat_from,  double lon_from, double lat_to, double lon_to) {
        var EARTH_RADIUS = 6371f; // Approx Earth radius in KM
        var dLat  = Math.toRadians(lat_to - lat_from);
        var dLong =  Math.toRadians(lon_to - lon_from);

        lat_from = Math.toRadians(lat_from);
        lat_to   =  Math.toRadians(lat_to);

        var a =  Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat_from) * Math.cos(lat_to) * Math.pow(Math.sin(dLong / 2), 2);
        var c =  (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        return EARTH_RADIUS * c;
    }

    //Todo
    public double distanceToTime(double dist, double speed) {
        return dist / speed;
    }

}
