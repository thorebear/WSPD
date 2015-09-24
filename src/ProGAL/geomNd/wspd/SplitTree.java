package ProGAL.geomNd.wspd;

import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;
import ProGAL.geomNd.BoundingBox;
import ProGAL.geomNd.Point;

import java.util.List;

public class SplitTree {

    private SplitTreeNode root;

    public SplitTree(Set<Point> points, BoundingBox rectangle) {
        root = calcSlowSplitTree(points, rectangle);
    }

    private SplitTreeNode calcSlowSplitTree(Set<Point> points, BoundingBox rectangle) {
        BoundingBox boundingBox = new BoundingBox(points);
        if (points.getSize() == 1){
            return new SplitTreeNode(boundingBox, rectangle, points);
        } else {
            int i = boundingBox.getDimensionWithMaxLength();
            double whereToSplit = boundingBox.getMiddleInDimension(i);
            Pair<BoundingBox, BoundingBox> rectanglePair = rectangle.split(i, whereToSplit);
            BoundingBox R1 = rectanglePair.fst;
            BoundingBox R2 = rectanglePair.snd;

            Set<Point> S1 = new Set<>();
            Set<Point> S2 = new Set<>();
            for(Point p : points){
                if (R1.contains(p)){
                    S1.insert(p);
                } else {
                    S2.insert(p);
                }
            }

            SplitTreeNode v = calcSlowSplitTree(S1, R1);
            SplitTreeNode w = calcSlowSplitTree(S2, R2);

            return new SplitTreeNode(v, w, boundingBox, rectangle, points);
        }
    }

    public List<SplitTreeNode> getAllInternalNodes() { return root.getAllInternalNodes(); }

}
