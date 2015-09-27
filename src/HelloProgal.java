import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;
import ProGAL.geom2d.Circle;
import ProGAL.geom2d.PointSet;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geomNd.BoundingBox;
import ProGAL.geomNd.Point;
import ProGAL.geomNd.wspd.SplitTree;
import ProGAL.geomNd.wspd.WellSeparatedPairDecomposition;

import java.awt.*;


public class HelloProgal {

    public static void main(String[] args) throws InterruptedException {
        //test2DimensionWSPD();
        testNDimensionWSPD();
    }

    private static void testNDimensionWSPD() {
        //Generate points
        Set<Point> points = new Set<>();
        for (int i = 0; i < 4; i++) {
            points.insert(new Point(new double[]{Math.random(), Math.random()}));
        }

        SplitTree splitTree = new SplitTree(points, new BoundingBox(points));
        //WellSeparatedPairDecomposition wspd = new WellSeparatedPairDecomposition(splitTree, 0.3);
        //System.out.print(wspd.toString());
    }

    private static void test2DimensionWSPD() {
        Set<ProGAL.geom2d.Point> points = new PointSet();
        for (int i = 0; i < 4; i++)
            points.insert(new ProGAL.geom2d.Point(Math.random(), Math.random()));

        //Display them
        J2DScene scene = J2DScene.createJ2DSceneInFrame();

        for (ProGAL.geom2d.Point p : points) {
            //A filled circle with radius 0.01 and border 0
            scene.addShape(new Circle(p, 0.01), Color.ORANGE, 0, true);
        }

        ProGAL.geom2d.BoundingBox box = new ProGAL.geom2d.BoundingBox(points);

        ProGAL.geom2d.wspd.SplitTree tree = new ProGAL.geom2d.wspd.SplitTree(points, box);
        //tree.toScene(scene);

        scene.centerCamera();
        ProGAL.geom2d.wspd.WellSeparatedPairDecomposition wspd = new ProGAL.geom2d.wspd.WellSeparatedPairDecomposition(tree, 0.5);
        wspd.toScene(scene);
        System.out.print(wspd.toString());
    }
}
