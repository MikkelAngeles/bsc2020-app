package mhel.itu.bachelor.shortestpathmap.algorithm;

public class SimpleVertex implements IVertex {
    public int i;
    public float x;
    public float y;

    public SimpleVertex(int i, float x, float y) {
        this.i = i;
        this.x = x;
        this.y = y;
    }

    @Override
    public int I() {
        return i;
    }

    @Override
    public float X() {
        return x;
    }

    @Override
    public float Y() {
        return y;
    }
}
