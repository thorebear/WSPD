package ProGAL.geom2d.wspd;

import ProGAL.geom2d.BoundingBox;
import ProGAL.geom2d.viewer.J2DScene;

import java.awt.*;

public class SplitTreeNode {

    private SplitTreeNode leftChild;
    private SplitTreeNode rightChild;
    private BoundingBox boundingBox;
    private BoundingBox rectangle;

    public SplitTreeNode(BoundingBox boundingBox, BoundingBox rectangle) {
        leftChild = null;
        rightChild = null;
        this.boundingBox = boundingBox;
        this.rectangle = rectangle;
    }

    public SplitTreeNode(SplitTreeNode leftChild, SplitTreeNode rightChild,
                     BoundingBox boundingBox, BoundingBox rectangle) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.boundingBox = boundingBox;
        this.rectangle = rectangle;
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

    public void toScene(J2DScene scene) {
        boundingBox.toScene(scene, Color.BLACK);
        rectangle.toScene(scene, Color.BLUE);

        if (leftChild != null)
            leftChild.toScene(scene);

        if (rightChild != null)
            rightChild.toScene(scene);
    }
}
