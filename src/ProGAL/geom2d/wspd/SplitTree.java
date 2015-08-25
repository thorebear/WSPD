package ProGAL.geom2d.wspd;

import ProGAL.dataStructures.Pair;
import ProGAL.geom2d.BoundingBox;
import ProGAL.geom2d.Line;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;

import java.util.ArrayList;
import java.util.List;

public class SplitTree {

    private SplitTreeNode root;

    public SplitTree(List<Point> points, BoundingBox rectangle) {
        root = calcSlowSplitTree(points, rectangle);
    }

    private SplitTreeNode calcSlowSplitTree(List<Point> points, BoundingBox rectangle) {
        if (points.size() == 1){
            BoundingBox boundingBox = new BoundingBox(points);
            return new SplitTreeNode(boundingBox, rectangle);
        } else {
            BoundingBox boundingBox = new BoundingBox(points);
            int i = boundingBox.getDimensionWithMaxLength();
            Line splitLine = boundingBox.getSplitLine(i);
            Pair<BoundingBox, BoundingBox> rectanglePair = rectangle.split(splitLine);
            BoundingBox R1 = rectanglePair.fst;
            BoundingBox R2 = rectanglePair.snd;

            List<Point> S1 = new ArrayList<>();
            List<Point> S2 = new ArrayList<>();

            for(Point p : points){
                if (R1.contains(p)){
                    S1.add(p);
                } else {
                    S2.add(p);
                }
            }

            SplitTreeNode v = calcSlowSplitTree(S1, R1);
            SplitTreeNode w = calcSlowSplitTree(S2, R2);

            return new SplitTreeNode(v, w, boundingBox, rectangle);
        }
    }

    public void toScene(J2DScene scene){
        root.toScene(scene);
    }
}
