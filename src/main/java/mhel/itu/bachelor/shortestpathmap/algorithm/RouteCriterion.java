package mhel.itu.bachelor.shortestpathmap.algorithm;

public class RouteCriterion {
    private RouteCriteriaEvaluationType evaluationType;
    private EdgePropSet set;
    private float weightFactor;

    public RouteCriterion(RouteCriteriaEvaluationType evaluationType, EdgePropSet set, float weightFactor) {
        this.evaluationType = evaluationType;
        this.set = set;
        this.weightFactor = weightFactor;
    }

    public RouteCriteriaEvaluationType getEvaluationType() {
        return evaluationType;
    }

    public EdgePropSet getSet() {
        return set;
    }

    public float getWeightFactor() {
        return weightFactor;
    }
}
