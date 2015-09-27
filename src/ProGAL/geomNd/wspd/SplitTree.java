package ProGAL.geomNd.wspd;

import ProGAL.dataStructures.*;
import ProGAL.geomNd.BoundingBox;
import ProGAL.geomNd.Point;

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
                    other.CrossPointers.put(d, pw);
                    pw.CrossPointers.put(di, other);
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
        private Map<Integer, PointWrapper> CrossPointers;
        private PointWrapper Copy;
        private PartialSplitTree.PartialSplitTreeNode Node;

        public PointWrapper(Point p) {
            this.point = p;
            this.CrossPointers = new HashMap<>();
        }

        public Point getPoint(){
            return point;
        }
    }

    private class PartialSplitTree {
        public PartialSplitTree(Set<Point> points, BoundingBox rectangle, Map<Integer, Set<PointWrapper>> LS) {
            int dimension = points.get(0).getDimensions();

            /// STEP 1: ///
            int n = points.getSize();
            int size = n;

            PartialSplitTreeNode u = new PartialSplitTreeNode(rectangle);

            Map<Integer, Set<PointWrapper>> CLS = new HashMap<>();

            for (int d = 0; d < dimension; d++) {
                Set<PointWrapper> CLS_d = new Set<PointWrapper>();
                Set<PointWrapper> LS_d = LS.get(d);
                for (int pi = 0; pi < LS_d.getSize(); pi++) {
                    Point p = LS_d.get(pi).getPoint();
                    PointWrapper pw = new PointWrapper(new Point(p.getCoords().clone()));
                    CLS_d.insert(pw);

                    // Insert pw as cross pointer in the same 'point' in all the other lists (which are initilized)
                    for (int di = 0; di < d; di++) {
                        PointWrapper other = CLS.get(di).get(pi);
                        // Add cross pointer in both directions:
                        other.CrossPointers.put(d, pw);
                        pw.CrossPointers.put(di, other);
                    }

                    // Set cross pointer to copy of point in CLS:
                    LS_d.get(pi).Copy = pw;
                }
                CLS.put(d, CLS_d);
            }

            for (int d = 0; d < dimension; d++) {
                Set<PointWrapper> points_d = CLS.get(d);
                // We don't need to sort CLS since LS is already sorted:
                for (int pi = 0; pi < points_d.getSize(); pi++) {
                    if (pi > 0) {
                        points_d.get(pi).Prev = points_d.get(pi - 1);
                    }
                    if (pi < points_d.getSize() - 1) {
                        points_d.get(pi).Next = points_d.get(pi + 1);
                    }
                }
            }

            while (size > n / 2) {
                /// STEP 3: ///
                BoundingBox boundingBox_u = new BoundingBox(points);
                int i = boundingBox_u.getDimensionWithMaxLength();
                double middle = boundingBox_u.getMiddleInDimension(i);
                Set<PointWrapper> LS_u_i = LS.get(i);
                PointWrapper p = LS_u_i.getFirst();
                PointWrapper p_prime = p.Next;
                PointWrapper q = LS_u_i.getLast();
                PointWrapper q_prime = q.Prev;
                int size_prime = 1;
                while (p_prime.getPoint().getCoord(i) <= middle
                        && q_prime.getPoint().getCoord(i) >= middle) {
                    p = p_prime;
                    p_prime = p.Next;
                    q = q_prime;
                    q_prime = q.Prev;
                    size_prime++;
                }

                Pair<BoundingBox, BoundingBox> pair = boundingBox_u.split(i, middle);
                BoundingBox rectangle_v = pair.fst;
                BoundingBox rectangle_w = pair.snd;

                PartialSplitTreeNode v = new PartialSplitTreeNode(rectangle_v);
                PartialSplitTreeNode w = new PartialSplitTreeNode(rectangle_w);

                u.setLeftChild(v);
                u.setRightChild(w);

                if (p_prime.getPoint().getCoord(i) >= middle) {
                    /// STEP 4: ///
                    boolean p_encounted = false;
                    int index = 0;
                    while(!p_encounted){
                        PointWrapper z = LS_u_i.get(index);

                        for(PointWrapper pw : z.CrossPointers.values()){
                            pw.Copy.Node = v;
                        }

                        z.Copy.Node = v;

                        for(int di = 0; di < z.CrossPointers.size(); di++){
                            PointWrapper crossPointer = z.CrossPointers.get(di);
                            Set<PointWrapper> LS_u_j = LS.get(di);
                            LS_u_j.delete(crossPointer);
                        }

                        LS_u_i.delete(z);

                        if (z.equals(p)){
                            p_encounted = true;
                        } else {
                            z = z.Next;
                        }
                        index++;
                    }

                    u = w;
                    size = size - size_prime;

                } else {
                    /// STEP 5: ///
                    boolean q_encounted = false;
                    int index = LS_u_i.getSize();
                    while(!q_encounted){
                        PointWrapper z = LS_u_i.get(index);

                        for(PointWrapper pw : z.CrossPointers.values()){
                            pw.Copy.Node = v;
                        }

                        z.Copy.Node = v;

                        for(int di = 0; di < z.CrossPointers.size(); di++){
                            PointWrapper crossPointer = z.CrossPointers.get(di);
                            Set<PointWrapper> LS_u_j = LS.get(di);
                            LS_u_j.delete(crossPointer);
                        }

                        LS_u_i.delete(z);

                        if(z.equals(q)){
                            q_encounted = true;
                        } else {
                            q = q.Prev;
                        }

                        index--;
                    }


                }
            }

            /// STEP 2: ///

        }

        private class PartialSplitTreeNode {
            private final BoundingBox rectangle;
            private PartialSplitTreeNode rightChild;
            private PartialSplitTreeNode leftChild;

            public PartialSplitTreeNode(BoundingBox rectangle) {
                this.rectangle = rectangle;
            }

            public void setLeftChild(PartialSplitTreeNode leftChild) {
                this.leftChild = leftChild;
            }

            public void setRightChild(PartialSplitTreeNode rightChild) {
                this.rightChild = rightChild;
            }
        }
    }
}
