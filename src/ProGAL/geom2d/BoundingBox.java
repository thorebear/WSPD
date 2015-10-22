package ProGAL.geom2d;

import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;
import ProGAL.geom2d.viewer.J2DScene;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoundingBox {

    private Point leftTop, leftBottom, rightTop, rightBottom;

    public BoundingBox(Point leftTop, Point leftBottom,
                       Point rightTop, Point rightBottom)
    {
        this.leftTop = leftTop;
        this.leftBottom = leftBottom;
        this.rightTop = rightTop;
        this.rightBottom = rightBottom;
    }

    public BoundingBox(Set<Point> points) {
        double leftBound, topBound, rightBound, bottomBound;
        if(points.isEmpty()){
            throw new IllegalArgumentException(
                    "Bounding box for empty point set is not defined");
        }

        // Use the first point to get init values
        leftBound = points.get(0).x();
        rightBound = points.get(0).x();
        topBound = points.get(0).y();
        bottomBound = points.get(0).y();

        // Iterates through the rest of the points, to find the bounds
        for(int i = 1; i < points.getSize(); i++){
            Point p = points.get(i);
            leftBound = Math.min(p.x(), leftBound);
            rightBound = Math.max(p.x(), rightBound);
            bottomBound = Math.min(p.y(), bottomBound);
            topBound = Math.max(p.y(), topBound);
        }

        leftTop = new Point(leftBound, topBound);
        leftBottom = new Point(leftBound, bottomBound);
        rightTop = new Point(rightBound, topBound);
        rightBottom = new Point(rightBound, bottomBound);
    }

    public void toScene(J2DScene scene, Color col) {
        double thick = 0.003;
        scene.addShape(new LineSegment(leftTop, leftBottom), col, thick);
        scene.addShape(new LineSegment(rightTop, rightBottom), col, thick);
        scene.addShape(new LineSegment(leftTop, rightTop), col, thick);
        scene.addShape(new LineSegment(leftBottom, rightBottom), col, thick);
    }

    public int getDimensionWithMaxLength(){
        if ((rightTop.x() - leftTop.x()) > (leftTop.y() - leftBottom.y())){
            return 0;
        } else {
            return 1;
        }
    }

    public boolean contains(Point p){
        return leftTop.x() <= p.x() && p.x() <= rightTop.x() && leftBottom.y() <= p.y() && p.y() <= leftTop.y();
    }

    public Line getSplitLine(int dimension) {
        if (dimension == 0){
            return new Line(new Point((rightTop.x() + leftTop.x()) / 2, 0),
                    new Vector(1, 0.0D));
        } else {
            return new Line(new Point(0, (leftTop.y() + leftBottom.y()) / 2),
                    new Vector(0.0D, 1));
        }
    }

    public Pair<BoundingBox, BoundingBox> split(Line splitLine) {
        if (splitLine.n.y() == 0.0D){

            if (splitLine.getPoint().x() < leftBottom.x() ||
                    splitLine.getPoint().x() > rightBottom.x())
            {
                throw new IllegalArgumentException("The split line does not intersect with the bounding box");
            }


            BoundingBox left = new BoundingBox(
                leftTop,
                leftBottom,
                new Point(splitLine.getPoint().x(), rightTop.y()),
                new Point(splitLine.getPoint().x(), rightBottom.y())
            );

            BoundingBox right = new BoundingBox(
                new Point(splitLine.getPoint().x(), leftTop.y()),
                new Point(splitLine.getPoint().x(), leftBottom.y()),
                rightTop,
                rightBottom
            );

            return new Pair<>(left, right);

        } else if (splitLine.n.x() == 0.0D){

            if (splitLine.getPoint().y() < leftBottom.y() ||
                    splitLine.getPoint().y() > leftTop.y())
            {
                throw new IllegalArgumentException("The split line does not intersect with the bounding box");
            }

            BoundingBox top = new BoundingBox(
                leftTop,
                new Point(leftBottom.x(), splitLine.getPoint().y()),
                rightTop,
                new Point(rightBottom.x(), splitLine.getPoint().y())
            );

            BoundingBox bottom = new BoundingBox(
                new Point(leftTop.x(), splitLine.getPoint().y()),
                leftBottom,
                new Point(rightTop.x(), splitLine.getPoint().y()),
                rightBottom
            );

            return new Pair<>(top, bottom);
        } else {
            throw new IllegalArgumentException(
                "A bounding box can only be split by a vertical or horizontal line");
        }
    }

    public Point getLeftTop() { return leftTop; }
    public Point getLeftBottom() { return leftBottom; }
    public Point getRightTop() { return rightTop; }
    public Point getRightBottom() { return rightBottom; }
    public List<Point> getAllCorners() {
        return new ArrayList<Point>() {{
            add(leftTop);
            add(leftBottom);
            add(rightTop);
            add(rightBottom);
        }};
    }

    public double getMaxLength() {
        return Math.max((rightTop.x() - leftTop.x()), (leftTop.y() - leftBottom.y()));
    }
}
