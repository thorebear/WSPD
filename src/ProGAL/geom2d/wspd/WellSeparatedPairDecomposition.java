package ProGAL.geom2d.wspd;

import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;
import ProGAL.geom2d.*;
import ProGAL.geom2d.Point;

import java.util.List;

public class WellSeparatedPairDecomposition {
    Set<Pair<Set<Point>, Set<Point>>> WSPD;

    public WellSeparatedPairDecomposition(SplitTree tree, Set<Point> points, double s) {
        WSPD = new Set<>();

        for(SplitTreeNode node : tree.getAllInternalNodes()){
            WSPD.append(findPairs(node.getLeftChild(), node.getRightChild(), s));
        }
    }

    public Set<Pair<Set<Point>, Set<Point>>>  findPairs(SplitTreeNode v, SplitTreeNode w, double s)
    {
        if (isWellSeparated(v, w, s)){
            return new Set<Pair<Set<Point>, Set<Point>>> () {{
                insert(new Pair<>(v.getPoints(), w.getPoints()));
            }};
        } else {
            Set<Pair<Set<Point>, Set<Point>>> pairs = new Set<>();
            if (v.getBoundingBox().getMaxLength() <= w.getBoundingBox().getMaxLength()){
                SplitTreeNode w_l = w.getLeftChild();
                SplitTreeNode w_r = w.getRightChild();

                pairs.append(findPairs(v, w_l, s));
                pairs.append(findPairs(v, w_r, s));
            } else {
                SplitTreeNode v_l = v.getLeftChild();
                SplitTreeNode v_r = v.getRightChild();

                pairs.append(findPairs(v_l, w, s));
                pairs.append(findPairs(v_r, w, s));
            }
            return pairs;
        }
    }

    public boolean isWellSeparated(SplitTreeNode v, SplitTreeNode w, double s){
        BoundingBox box_v = v.getBoundingBox();
        Circle c_v = new Circle(box_v.getLeftBottom(), box_v.getLeftTop(), box_v.getRightBottom());

        BoundingBox box_w = w.getBoundingBox();
        Circle c_w = new Circle(box_w.getLeftBottom(), box_w.getLeftTop(), box_w.getRightBottom());

        Circle minCircle, maxCircle;

        BoundingBox minBox;
        if (c_v.getRadius() > c_w.getRadius()){
            minCircle = c_w;
            maxCircle = c_v;
            minBox = box_w;
        } else {
            minCircle = c_v;
            maxCircle = c_w;
            minBox = box_v;
        }

        List<Point> allCorners = minBox.getAllCorners();
        Point minDistanceToOtherCenterPoint = allCorners.remove(0);
        for (Point p : allCorners)
        {
            if (p.distance(maxCircle.getCenter()) < minDistanceToOtherCenterPoint.distance(maxCircle.getCenter())){
                minDistanceToOtherCenterPoint = p;
            }
        }

        Vector directionVector = new Vector(maxCircle.getCenter(), minCircle.getCenter());
        double radiusDiff = maxCircle.getRadius() - minCircle.getRadius();
        minCircle.setRadius(maxCircle.getRadius());
        minCircle.translate(directionVector.scaleToLength(radiusDiff));


        double distanceBetweenCircles = maxCircle.getCenter().distance(minCircle.getCenter()) -
                (maxCircle.getRadius() + minCircle.getRadius());

        return distanceBetweenCircles >= s*maxCircle.getRadius();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Pair<Set<Point>, Set<Point>> pair : WSPD){
            for(Point p : pair.fst){
                builder.append(p.toString());
            }
            builder.append(" :: ");
            for(Point p : pair.snd){
                builder.append(p.toString());
                builder.append(",");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
