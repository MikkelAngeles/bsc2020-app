package mhel.itu.bachelor.shortestpathmap.algorithm;

import java.util.ArrayList;
import java.util.List;

public class RouteQuery {
    int source;
    int target;
    List<RouteCriterion> criteria;

    public RouteQuery() {
        this.criteria = new ArrayList<>();
    }

    public RouteQuery(int source, int target) {
        this.source = source;
        this.target = target;
        this.criteria = new ArrayList<>();
    }

    public RouteQuery(int source, int target, List<RouteCriterion> criteria) {
        this.source = source;
        this.target = target;
        this.criteria = criteria;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public List<RouteCriterion> getCriteria() {
        return criteria;
    }

    public void addCriterion(RouteCriteriaEvaluationType eval, EdgePropSet set, float weightFactor) {
        criteria.add(new RouteCriterion(eval, set, weightFactor));
    }
}
