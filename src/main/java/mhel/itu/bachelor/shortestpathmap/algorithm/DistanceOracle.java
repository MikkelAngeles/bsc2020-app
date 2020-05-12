package mhel.itu.bachelor.shortestpathmap.algorithm;

import mhel.itu.bachelor.shortestpathmap.model.*;

import java.util.Comparator;
import java.util.List;

import static java.lang.Double.isNaN;

public class DistanceOracle {
    private IDataModel dataModel;
    private double distUpper    = 1;  //The highest recorded value from the physical distance table
    private double timeUpper    = 1;  //The highest recorded value from the time distance table
    private double speedLower   = 0;  //The lowest recorded speed from the edge properties.

    public DistanceOracle() { }

    public DistanceOracle(IDataModel dataModel) {
        this.dataModel = dataModel;

        var dMax = Double.NEGATIVE_INFINITY;
        var tMax = Double.NEGATIVE_INFINITY;
        var sMin = Double.POSITIVE_INFINITY;

        for(var i = 0; i < dataModel.E(); i++) {
            var dist = dataModel.getDistanceTable()[i];
            if (dist > dMax) dMax = dist;

            var time = dataModel.getTimeTable()[i];
            if(time > tMax) tMax = time;

            var speed = distTimeToSpeed(dist, time);
            if(speed < sMin) sMin = speed;
        }

        distUpper   = dMax;
        timeUpper   = tMax;
        speedLower  = sMin;

       /* dataModel.getPropertyMap()
                .get("maxspeed")
                .stream()
                .min(Comparator.comparingInt(Integer::parseInt))
                .ifPresent(rs -> speedLower = Integer.parseInt(rs));*/
    }

    public double cost(int edge, RouteQuery q) {
        if(q.getCriteria() == null || q.getCriteria().isEmpty()) return dist(edge); //Default is physical distance.

        var eDist = dist(edge);
        var eTime = time(edge);

        var distAccum = 0;
        var distCount = 0;
        var timeAccum = 0;
        var timeCount = 0;

        for(var c : q.getCriteria()) {
            var curr_key = c.getProperty().getKey();
            var curr_value = c.getProperty().getValue();

            if(!dataModel.hasEdgeProperty(edge, c.getProperty())) continue;
            var prop = dataModel.getEdgeProperty(edge, curr_key);

            if(!prop.getValue().equals(curr_value)) continue;
            var factor = c.getWeightFactor();
            if(c.getWeightType().equals(EdgeWeightType.DISTANCE)) {
                distAccum += factor;
                distCount++;
            }
            else if(c.getWeightType().equals(EdgeWeightType.TIME)) {
                timeAccum += factor;
                timeCount++;
            }
        }

        var distScaled = distCount > 0 ? (eDist * (distAccum / distCount)) : eDist;
        var timeScaled = timeCount > 0 ? (eTime * (timeAccum / timeCount)) : eTime;

        var sum = timeCount > 0 ? (distCount > 0 ? (timeScaled + distScaled) : timeScaled) : distScaled;
        if(sum < 0) throw new IllegalArgumentException("Cost can't be negative!");
        return sum;
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
        double max = 0;
        if(n == t) return max;
        for(var l : dataModel.getLandmarksDistanceTable().entrySet()) {
            var distTable = l.getValue(); //Distance table for all vertices to landmark l

            var nDistToLandmark = distTable.get(n); //True precomputed distance from vertex n to landmark l
            var tDistToLandmark = distTable.get(t); //True precomputed distance from vertex t to landmark l

            //Skip landmark if n or t has no path to l
            if(nDistToLandmark == null || tDistToLandmark == null) continue;

            //Absolute delta value between distances
            double dist = Math.abs(nDistToLandmark - tDistToLandmark);

            max = Math.max(max, dist);
        }

        if(max < 0) throw new IllegalArgumentException("Landmark distance cannot be negative!");
        if(isNaN(max)) throw new IllegalArgumentException("Landmark distance is not a number!");
        return max;
    }

    public double haversine(int v, int w) {
        var n = dataModel.getVertex(v);
        var t = dataModel.getVertex(w);
        var tmp = haversine(n.X(), n.Y(), t.X(), t.Y());
        return getDistFactor(tmp);
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6372.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double errorFactor = (1 - 0.055);
        return (R * c) * errorFactor;
    }

    public static double distSpeedToTime(double dist, double speed) {
        return dist / speed;
    }

    public static double distTimeToSpeed(double dist, double time) {
        return dist / time;
    }

    public double haversineTime(int v, int w) {
        var n = dataModel.getVertex(v);
        var t = dataModel.getVertex(w);
        var tmp = haversine(n.X(), n.Y(), t.X(), t.Y());
        return getTimeFactor(distSpeedToTime(tmp, speedLower));
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

}
