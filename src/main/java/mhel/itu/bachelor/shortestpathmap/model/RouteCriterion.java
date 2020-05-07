package mhel.itu.bachelor.shortestpathmap.model;

public class RouteCriterion {
    private EdgeWeightType weightType;
    private EdgeProperty property;
    private double weightFactor;

    public RouteCriterion(EdgeWeightType weightType, EdgeProperty property, double weightFactor) {
        this.weightType     = weightType;
        this.property       = property;
        this.weightFactor   = weightFactor;
    }

    public EdgeWeightType getWeightType() {
        return weightType;
    }

    public EdgeProperty getProperty() {
        return property;
    }

    public double getWeightFactor() {
        return weightFactor;
    }
}
