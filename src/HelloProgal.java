import ProGAL.dataStructures.Set;
import ProGAL.geomNd.Point;
import ProGAL.geomNd.BoundingBox;


public class HelloProgal {

    public static void main(String[] args) throws InterruptedException {
        //Generate points
        Set<Point> points = new Set<>();
        for (int i = 0; i < 20; i++) {
            points.insert(new Point(new double[]{Math.random(), Math.random()}));
        }

        BoundingBox box = new BoundingBox(points);

        System.out.println(box.toString());
    }
}
