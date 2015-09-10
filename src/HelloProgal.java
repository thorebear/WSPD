import ProGAL.dataStructures.Set;
import ProGAL.geom2d.*;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom2d.wspd.SplitTree;
import ProGAL.geom2d.wspd.WellSeparatedPairDecomposition;

import java.awt.*;

public class HelloProgal {

    public static void main(String[] args) throws InterruptedException {
        //Generate points
        Set<Point> points = new PointSet();
        for (int i = 0; i < 10; i++)
            points.insert(new Point(Math.random(), Math.random()));

        //Display them
        J2DScene scene = J2DScene.createJ2DSceneInFrame();

        for (Point p : points) {
            //A filled circle with radius 0.01 and border 0
            scene.addShape(new Circle(p, 0.01), Color.ORANGE, 0, true);
        }

        BoundingBox box = new BoundingBox(points);

        SplitTree tree = new SplitTree(points, box);
        //tree.toScene(scene);

        scene.centerCamera();
        WellSeparatedPairDecomposition wspd = new WellSeparatedPairDecomposition(tree, points, 0.5);
        System.out.print(wspd.toString());
    }
}
