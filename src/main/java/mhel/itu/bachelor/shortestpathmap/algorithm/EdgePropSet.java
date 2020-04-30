package mhel.itu.bachelor.shortestpathmap.algorithm;

public class EdgePropSet {
    EdgePropKey type;
    EdgePropValue prop;

    public EdgePropSet(EdgePropKey type, EdgePropValue prop) {
        this.type = type;
        this.prop = prop;
    }

    public EdgePropKey getType() {
        return type;
    }

    public void setType(EdgePropKey type) {
        this.type = type;
    }

    public EdgePropValue getProp() {
        return prop;
    }

    public void setProp(EdgePropValue prop) {
        this.prop = prop;
    }
}
