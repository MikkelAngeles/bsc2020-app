package mhel.itu.bachelor.shortestpathmap.algorithm;

public class SimpleEdge implements IEdge {
    private int index;
    private IVertex from;
    private IVertex to;

    public SimpleEdge(int i, IVertex from, IVertex to) {
        this.index = i;
        this.from = from;
        this.to = to;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public IVertex from() {
        return from;
    }

    @Override
    public IVertex to() {
        return to;
    }
}
