package mhel.itu.bachelor.shortestpathmap.model;

import java.io.Serializable;

public class SimpleVertex implements IVertex, Serializable {
    public int i;
    public double x;
    public double y;

    public SimpleVertex(int i, double x, double y) {
        this.i = i;
        this.x = x;
        this.y = y;
    }

    @Override
    public int I() {
        return i;
    }

    @Override
    public double X() {
        return x;
    }

    @Override
    public double Y() {
        return y;
    }
}
