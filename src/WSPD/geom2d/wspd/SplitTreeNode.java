package WSPD.geom2d.wspd;

import ProGAL.dataStructures.Set;
import WSPD.geom2d.BoundingBox;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class SplitTreeNode {

    private Set<Point> points;
    private SplitTreeNode leftChild;
    private SplitTreeNode rightChild;
    private BoundingBox boundingBox;
    private BoundingBox rectangle;

    public SplitTreeNode(BoundingBox boundingBox, BoundingBox rectangle, Set<Point> points) {
        leftChild = null;
        rightChild = null;
        this.boundingBox = boundingBox;
        this.rectangle = rectangle;
        this.points = points;
    }

    public SplitTreeNode(SplitTreeNode leftChild, SplitTreeNode rightChild,
                     BoundingBox boundingBox, BoundingBox rectangle, Set<Point> points) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.boundingBox = boundingBox;
        this.rectangle = rectangle;
        this.points = points;
    }

    public SplitTreeNode getLeftChild(){
        return leftChild;
    }

    public SplitTreeNode getRightChild() {
        return rightChild;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public BoundingBox getRetangle() {
        return rectangle;
    }

    public Set<Point> getPoints() { return points; }

    public void toScene(J2DScene scene, Color color) {
//        Color nxtLevelColor = new Color((int)(Math.random() * 0x1000000));
//        if (leftChild != null)
//            leftChild.toScene(scene, nxtLevelColor);
//
//        if (rightChild != null)
//            rightChild.toScene(scene, nxtLevelColor);

        boundingBox.toScene(scene, color);
        rectangle.toScene(scene, color);

        rectangle.getSplitLine(rectangle.getDimensionWithMaxLength()).toScene(scene, 3,Color.GRAY, 0.01);

        rightChild.boundingBox.toScene(scene, color);
        leftChild.boundingBox.toScene(scene, color);
    }

    // Gets all internal nodes in the subtree with root in this node.
    public List<SplitTreeNode> getAllInternalNodes()
    {
        SplitTreeNode leftChild = getLeftChild();
        SplitTreeNode rightChild = getRightChild();

        List<SplitTreeNode> nodes = new ArrayList<>();
        if(leftChild != null || rightChild != null){
            nodes.add(this);
            if (leftChild != null) {
                nodes.addAll(leftChild.getAllInternalNodes());
            }
            if (rightChild != null) {
                nodes.addAll(rightChild.getAllInternalNodes());
            }
        }
        return nodes;
    }
}