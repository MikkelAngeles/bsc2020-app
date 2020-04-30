package mhel.itu.bachelor.shortestpathmap.algorithm;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class EnhancedShape implements Comparable, Serializable {
    private Shape shape;
    private Point2D median;
    private Rectangle2D bounds;
    public static boolean vertical;
    public int graphIndex;

    public int getGraphIndex() {
        return graphIndex;
    }

    public void setGraphIndex(int graphIndex) {
        this.graphIndex = graphIndex;
    }

    public EnhancedShape(Shape shape, int graphIndex) {
        this.shape = shape;
        this.graphIndex = graphIndex;
        bounds = shape.getBounds2D();
        median = new Point2D.Float((float)bounds.getCenterX(), (float)bounds.getCenterY());
    }

    public Shape getShape() {
        return shape;
    }

    public Rectangle2D getBounds(){
        return this.bounds;
    }

    public Point2D getMedian() {
        return median;
    }

    public float getDistanceToCenter(Point2D center) {
        if(shape.intersects(center.getX(), center.getY(), 0.00001, 0.00001)){
            return 0;
        }
        float smallest=Float.MAX_VALUE;
        for(PathIterator pi=shape.getPathIterator(null); !pi.isDone(); pi.next()){
            float[] coords=new float[6];
            pi.currentSegment(coords);
            smallest=Float.min(smallest,(float) Math.sqrt(Math.pow(coords[0]-center.getX(), 2f)+Math.pow(coords[1]-center.getY(), 2f)));
        }
        return smallest;
    }

    @Override
    public int compareTo(Object o) {
        EnhancedShape that = (EnhancedShape)o;
        return compare(that, vertical);
    }
    public int compare(EnhancedShape that, boolean vertical){
        if(vertical){
            if(this.median.getX() < that.getMedian().getX()) return -1;
            if(this.median.getX() > that.getMedian().getX()) return 1;
            return 0;
        }
        if(this.median.getY() < that.getMedian().getY()) return -1;
        if(this.median.getY() > that.getMedian().getY()) return 1;
        return 0;
    }

    public static void setVertical(boolean vertical) {
        EnhancedShape.vertical = vertical;
    }
}
