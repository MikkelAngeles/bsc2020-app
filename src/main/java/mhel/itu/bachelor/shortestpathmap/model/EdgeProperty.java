package mhel.itu.bachelor.shortestpathmap.model;

import java.io.Serializable;

public class EdgeProperty implements Comparable, Serializable {
    String key;
    String value;

    public EdgeProperty(String key) {
        this.key = key;
    }

    public EdgeProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        var that = (EdgeProperty)o;
        return compare(that);
    }

    public int compare(EdgeProperty that){
        if(this.getKey().equals(that.getKey())) return 1;
        else return 0;
    }
}
