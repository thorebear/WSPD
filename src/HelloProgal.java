import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;
import ProGAL.geomNd.Point;
import ProGAL.geomNd.BoundingBox;


public class HelloProgal {

    public static void main(String[] args) throws InterruptedException {
        //Generate points
        Set<Point> points = new Set<>();
        for (int i = 0; i < 5; i++) {
            points.insert(new Point(new double[]{Math.random(), Math.random(), Math.random()}));
        }

        BoundingBox box = new BoundingBox(points);
        System.out.println("Max length dim: " + box.getDimensionWithMaxLength());
        System.out.println("Num of corners: " + box.getCornerPoints().size());
        Pair<BoundingBox, BoundingBox> boxes = box.split(box.getDimensionWithMaxLength());
        System.out.println(box.toString());
        System.out.println("Splitted 1: ");
        System.out.println(boxes.fst.toString());
        System.out.println("Splitted 2: ");
        System.out.println(boxes.snd.toString());
    }
}
