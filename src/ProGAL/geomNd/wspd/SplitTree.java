package ProGAL.geomNd.wspd;

import ProGAL.dataStructures.*;
import ProGAL.geomNd.BoundingBox;
import ProGAL.geomNd.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitTree {

    private SplitTreeNode root;

    public SplitTree(Set<Point> points, BoundingBox rectangle) {

        //root = calcSlowSplitTree(points, rectangle);
        root = calcFastSplitTree(points, rectangle);
    }

    private SplitTreeNode calcFastSplitTree(Set<Point> points, BoundingBox rectangle) {
        if (points.getSize() == 1)
            return calcSlowSplitTree(points, rectangle);

        int dimension = points.get(0).getDimensions();

        Map<Integer,Set<PointWrapper>> LS = new HashMap<>();
        // Preprocessing step

        // 1: Make the list for points
        for(int d = 0; d < dimension; d++){
            Set<PointWrapper> points_d = new Set<>();
            for(int pi = 0; pi < points.getSize(); pi++){
                Point p = points.get(pi);
                PointWrapper pw = new PointWrapper(p);
                points_d.insert(pw);

                // Insert pw as cross pointer in the same 'point' in all the other lists (which are initilized)
                for(int di = 0; di < d; di++){
                    PointWrapper other = LS.get(di).get(pi);
                    // Add cross pointer in both directions:
                    other.CrossPointers.insert(pw);
                    pw.CrossPointers.insert(other);
                }
            }

            LS.put(d, points_d);
        }

        // sorting
        for(int d = 0; d < dimension; d++) {
            Set<PointWrapper> points_d = LS.get(d);
            SorterQuick sorter = new SorterQuick();
            sorter.Sort(points_d, new SortToolPointInDimension(d));

            // When the list is sorted, we can set the prev/next information
            for(int pi = 0; pi < points_d.getSize(); pi++){
                if (pi > 0) {
                    points_d.get(pi).Prev = points_d.get(pi - 1);
                }
                if (pi < points_d.getSize() - 1) {
                    points_d.get(pi).Next = points_d.get(pi + 1);
                }
            }

            // Update the lists in LS
            LS.put(d, points_d);
        }

        PartialSplitTree partialSplitTree = new PartialSplitTree(points, rectangle, LS);
        return null;
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

    public class PointWrapper {
        private Point point;
        private PointWrapper Prev;
        private PointWrapper Next;
        private Set<PointWrapper> CrossPointers;

        public PointWrapper(Point p) {
            this.point = p;
            this.CrossPointers = new Set<>();
        }

        public Point getPoint(){
            return point;
        }
    }

    private class PartialSplitTree {
        public PartialSplitTree(Set<Point> points, BoundingBox rectangle, Map<Integer, Set<PointWrapper>> LS) {
        }
    }
}
