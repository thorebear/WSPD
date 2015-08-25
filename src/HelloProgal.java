import ProGAL.dataStructures.Pair;
import ProGAL.geom2d.*;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom2d.wspd.SplitTree;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HelloProgal {

    public static void main(String[] args){
        //Generate points
        List<Point> points = new ArrayList<>();
        for(int i=0;i<10;i++)
            points.add(new Point(Math.random(), Math.random()));

        //Display them
        J2DScene scene = J2DScene.createJ2DSceneInFrame();

        for(Point p: points){
            //A filled circle with radius 0.01 and border 0
            scene.addShape(new Circle(p, 0.01), Color.GREEN, 0, true);
        }

        BoundingBox box = new BoundingBox(points);

        SplitTree tree = new SplitTree(points, box);
        tree.toScene(scene);

        scene.centerCamera();
    }
}
