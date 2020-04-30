package mhel.itu.bachelor.shortestpathmap.algorithm;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class KdTree implements Serializable {
    private int size = 0;
    private Node root;
    private static final int maxLeafSize = 1000;

    // For vizualisation
    public List<SplitNode> splits = new ArrayList<>();

    public class SplitNode implements Serializable{
        public boolean vertical;
        public EnhancedShape shape;

        SplitNode(boolean vertical, EnhancedShape shape) {
            this.vertical = vertical;
            this.shape    = shape;
        }
    }

    public List<SplitNode> getSplits() {
        return splits;
    }

    //////////////////////////////////////////////////////
    //Naive KD-Tree
    //////////////////////////////////////////////////////

    public KdTree(List<EnhancedShape> shapes) {
        sortNodes(shapes, root = new Node(true));
    }

    private void sort(List<EnhancedShape> shapes, boolean vertical) {
        EnhancedShape.setVertical(vertical);
        Collections.sort(shapes);
    }

    public void sortNodes(List<EnhancedShape> shapes, Node node) {
        size++;
        if(shapes.size() <= maxLeafSize) {
            node.leaf = new Leaf(shapes);
            return;
        }

        //Sort by axis
        sort(shapes, node.vertical);

        //New split
        int mid = shapes.size() / 2;
        node.split = shapes.get(mid);
        node.left  = new Node(!node.vertical);
        node.right = new Node(!node.vertical);

        //Split list into into sublists
        List<EnhancedShape> leftList  = new ArrayList<>(mid);
        List<EnhancedShape> rightList = new ArrayList<>(mid);

        //Copy into subarrays
        float splitValue   = (node.vertical) ? (float) node.split.getMedian().getX() : (float) node.split.getMedian().getY();
        node.boundsMax = splitValue;
        node.boundsMin = splitValue;
        for (int i = 0; i < mid; i++) {
            setMaxBounds(node, shapes.get(i));
            leftList.add(shapes.get(i));

            setMinBounds(node, shapes.get(mid+i));
            rightList.add(shapes.get(mid+i));
        }

        //Recursive sorting
        sortNodes(leftList, node.left);
        sortNodes(rightList, node.right);

        //For visualization
        splits.add(new SplitNode(node.vertical, node.split));
    }
    private void setMaxBounds(Node node, EnhancedShape shape){
        float valueMax = (node.vertical) ? (float) shape.getBounds().getMaxX() : (float) shape.getBounds().getMaxY();
        if (node.boundsMax < valueMax) {
            node.boundsMax = valueMax;
        }
    }

    private void setMinBounds(Node node, EnhancedShape shape){
        float valueMin = (node.vertical) ? (float) shape.getBounds().getMinX(): (float) shape.getBounds().getMinY();
        if(node.boundsMin > valueMin){
            node.boundsMin = valueMin;
        }
    }

    private class Node implements Serializable {
        float boundsMin, boundsMax;
        EnhancedShape split;
        Node left, right;
        Leaf leaf;
        boolean vertical;

        public Node(boolean vertical){
            this.vertical = vertical;
        }
    }

    private class Leaf implements Serializable {
        List<EnhancedShape> shapes;
        public Leaf(List<EnhancedShape> shapes) {
            this.shapes = shapes;
        }
    }

    public List<EnhancedShape> rangeSearch(float xMin, float yMin, float xMax, float yMax){
        List<EnhancedShape> shapes = new ArrayList<>();
        Rectangle2D rect = new Rectangle2D.Float();
        float width      = Math.abs(Math.abs(xMax)-Math.abs(xMin));
        float height     = Math.abs(Math.abs(yMax)-Math.abs(yMin));

        rect.setRect(xMin, yMin, width, height);
        getShapes(shapes, root, true, xMin, yMin, xMax, yMax, rect);
        return shapes;
    }

    public List<EnhancedShape> getAllShapes(){
        return getLeafs(root);
    }

    private List<EnhancedShape> getLeafs(Node node){
        List<EnhancedShape> shapes = new ArrayList<>();
        if(node != null){
            shapes = (getLeafs(node.left));
            shapes.addAll(getLeafs(node.right));
            if(node.leaf != null) shapes.addAll(node.leaf.shapes);
        }
        return shapes;
    }

    public void getShapes(List<EnhancedShape> shapes, Node node, boolean vertical, float xMin, float yMin, float xMax, float yMax, Rectangle2D rect) {
        if (shapes == null) return;
        if (node.leaf != null) {
            for (EnhancedShape shape : node.leaf.shapes) {
                if(shape.getBounds().intersects(rect)) shapes.add(shape);
            }
            return;
        }

        if (vertical) {
            if (xMin < node.split.getBounds().getCenterX()) {
                getShapes(shapes, node.left, false, xMin, yMin, xMax, yMax, rect);
                if(xMax>node.boundsMin && xMax < node.split.getBounds().getCenterX())
                    getShapes(shapes, node.right, false, xMin, yMin, xMax, yMax, rect);
            }
            if (xMax > node.split.getBounds().getCenterX()) {
                getShapes(shapes, node.right, false, xMin, yMin, xMax, yMax, rect);
                if (xMin < node.boundsMax && xMin > node.split.getBounds().getCenterX())
                    getShapes(shapes, node.left, false, xMin, yMin, xMax, yMax, rect);
            }
        } else {
            if (yMin < node.split.getBounds().getCenterY()) {
                getShapes(shapes, node.left, true, xMin, yMin, xMax, yMax, rect);
                if (yMax > node.boundsMin && yMax < node.split.getBounds().getCenterY())
                    getShapes(shapes, node.right, false, xMin, yMin, xMax, yMax, rect);
            }
            if (yMax > node.split.getBounds().getCenterY()) {
                getShapes(shapes, node.right, true, xMin, yMin, xMax, yMax, rect);
                if (yMin < node.boundsMax && yMin > node.split.getBounds().getCenterY())
                    getShapes(shapes, node.left, false, xMin, yMin, xMax, yMax, rect);
            }
        }
    }

    public EnhancedShape getNearestNeighbor(float xMin, float yMin, float xMax, float yMax) {
        List<EnhancedShape> list = rangeSearch(xMin, yMin, xMax, yMax);
        Point2D center = new Point2D.Float((xMax + xMin)/2, (yMax + yMin)/2);
        return findClosestShape(list, center);
    }

    private EnhancedShape findClosestShape(List<EnhancedShape> list, Point2D center) {
        EnhancedShape nearest = null;
        float dist = Float.MAX_VALUE;
        float distanceToCenter;

        for(EnhancedShape shape: list) {
            distanceToCenter = shape.getDistanceToCenter(center);
            if(distanceToCenter < dist) {
                nearest = shape;
                dist    = distanceToCenter;
            }
        }
        return nearest;
    }
}
