package ProGAL.geom2d;

import ProGAL.geom2d.viewer.J2DScene;

import java.awt.*;
import java.util.List;

public class BoundingBox {

    private Point leftTop, leftBottom, rightTop, rightBottom;

    public BoundingBox(List<Point> points) {
        double leftBound, topBound, rightBound, bottomBound;
        if(points.isEmpty()){
            throw new IllegalArgumentException("Bounding box for empty point set is not defined");
        }

        // Use the first point to get init values
        leftBound = points.get(0).x();
        rightBound = points.get(0).x();
        topBound = points.get(0).y();
        bottomBound = points.get(0).y();

        // Iterates through the rest of the points, to find the bounds
        for(int i = 1; i < points.size(); i++){
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
        scene.addShape(new LineSegment(leftTop, leftBottom), col);
        scene.addShape(new LineSegment(rightTop, rightBottom), col);
        scene.addShape(new LineSegment(leftTop, rightTop), col);
        scene.addShape(new LineSegment(leftBottom, rightBottom), col);
    }
}
