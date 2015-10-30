package ndsamples;

import ProGAL.dataStructures.Set;
import ProGAL.geom2d.Circle;
import ProGAL.geom2d.PointSet;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geomNd.Point;
import WSPD.geom2d.BoundingBox;
import WSPD.geom2d.wspd.SplitTree;
import WSPD.geom2d.wspd.WellSeparatedPairDecomposition;

import java.awt.*;


public class slowsplittree {

    public static void main(String[] args) throws InterruptedException {
        testNDimensionWSPD();
    }

    private static void testNDimensionWSPD() {
        //Generate points
        Set<Point> points = new Set<>();
        for (int i = 0; i < 5000; i++) {
            points.insert(new Point(new double[]{Math.random(),Math.random(),Math.random(),Math.random(), Math.random(), Math.random()}));
        }

        // Compute split tree with O(n^2) algorithm
        WSPD.geomNd.wspd.SplitTree splitTree = new WSPD.geomNd.wspd.SplitTree(points, new WSPD.geomNd.BoundingBox(points), false);

        // Compute the Well-Separated Pair Decomposition
        WSPD.geomNd.wspd.WellSeparatedPairDecomposition wspd = new WSPD.geomNd.wspd.WellSeparatedPairDecomposition(splitTree, 0.1);

        System.out.println(wspd.toString());
    }
}
