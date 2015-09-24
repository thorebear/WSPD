import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;
import ProGAL.geom2d.Circle;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geomNd.BoundingBox;
import ProGAL.geomNd.Point;
import ProGAL.geomNd.wspd.SplitTree;
import ProGAL.geomNd.wspd.WellSeparatedPairDecomposition;

import java.awt.*;


public class HelloProgal {

    public static void main(String[] args) throws InterruptedException {
        //Generate points
        Set<Point> points = new Set<>();
        for (int i = 0; i < 2900; i++) {
            points.insert(new Point(new double[]{Math.random(), Math.random(),Math.random(), Math.random(),Math.random(), Math.random(), Math.random()}));
        }

        BoundingBox box = new BoundingBox(points);
        System.out.println(box.toString());


        SplitTree splitTree = new SplitTree(points, new BoundingBox(points));
        WellSeparatedPairDecomposition wspd = new WellSeparatedPairDecomposition(splitTree, 0.3);
        System.out.print(wspd.toString());


        //Generate points
//        Set<Point> points = new PointSet();
//        for (int i = 0; i < 20; i++)
//            points.insert(new Point(Math.random(), Math.random()));
//
//        //Display them
//        J2DScene scene = J2DScene.createJ2DSceneInFrame();
//
//        for (Point p : points) {
//            //A filled circle with radius 0.01 and border 0
//            scene.addShape(new Circle(p, 0.01), Color.ORANGE, 0, true);
//        }
//
//        BoundingBox box = new BoundingBox(points);
//
//        SplitTree tree = new SplitTree(points, box);
//        //tree.toScene(scene);
//
//        scene.centerCamera();
//        WellSeparatedPairDecomposition wspd = new WellSeparatedPairDecomposition(tree, points, 0.5);
//        wspd.toScene(scene);
//        System.out.print(wspd.toString());
    }
}
