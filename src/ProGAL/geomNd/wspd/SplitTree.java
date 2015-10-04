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

        private PartialSplitTreeNode root;

        public PartialSplitTree(Set<Point> points, BoundingBox rectangle, Map<Integer, Set<PointWrapper>> LS) {
            int dimension = points.get(0).getDimensions();

            /// STEP 1: ///
            int n = points.getSize();
            int size = n;

            PartialSplitTreeNode u = new PartialSplitTreeNode(rectangle);
            root = u;

            Map<Integer, Set<PointWrapper>> CLS = new HashMap<>();

            for (int d = 0; d < dimension; d++) {
                Set<PointWrapper> CLS_d = new Set<PointWrapper>();
                Set<PointWrapper> LS_d = LS.get(d);
                for (int pi = 0; pi < LS_d.getSize(); pi++) {
                    Point p = LS_d.get(pi).getPoint();
                    PointWrapper pw = new PointWrapper(new Point(p.getCoords().clone()));
                    CLS_d.insert(pw);

                    // Insert pw as cross pointer in the same 'point' in all the other lists (which are initilized)
                    // TODO THIS IS A BUG, THE POINTS ARE SORTED DIFFENT, SO THIS POINTS WONT HAVE THE SAME INDEX,
                    // TODO IN THE DIFFRENT LISTS
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
                // Since the points are sorted, we only need the first and the last point in each dimension,
                // to compute the bounding box:
                Set<Point> pointsNeedForBB = new Set<>();
                for(int d = 0; d < dimension; d++){
                    pointsNeedForBB.insert(LS.get(d).getFirst().getPoint());
                    pointsNeedForBB.insert(LS.get(d).getLast().getPoint());
                }
                BoundingBox boundingBox_u = new BoundingBox(pointsNeedForBB);
                u.setBoundingBox(boundingBox_u);

                int i = boundingBox_u.getDimensionWithMaxLength();
                System.out.println("Max index " + i);
                double middle = boundingBox_u.getMiddleInDimension(i);
                System.out.println("SIZE: " + size);
                Set<PointWrapper> LS_u_i = LS.get(i);
                PointWrapper p = LS_u_i.getFirst();
                PointWrapper p_prime = p.Next;
                PointWrapper q = LS_u_i.getLast();
                PointWrapper q_prime = q.Prev;
                int size_prime = 1;
                System.out.println("is null ? P_prime = " + (p_prime == null));
                System.out.println("is null ? Q_prime = " + (q_prime == null));
                System.out.println("is null ? P_prime.getpoint = " + (p_prime.getPoint() == null));
                System.out.println("is null ? Q_prime.getpoint = " + (q_prime.getPoint() == null));
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
                    PointWrapper z = LS_u_i.getFirst();
                    while (!p_encounted) {

                        System.out.println(z.CrossPointers.values().size());
                        for (PointWrapper pw : z.CrossPointers.values()) {
                            pw.Copy.Node = v;
                        }

                        z.Copy.Node = v;

                        for (int di = 0; di < z.CrossPointers.size(); di++) {
                            if (di == i)
                                continue;

                            PointWrapper crossPointer = z.CrossPointers.get(di);
                            Set<PointWrapper> LS_u_j = LS.get(di);
                            LS_u_j.delete(crossPointer);
                        }

                        LS_u_i.delete(z);

                        if (z.equals(p)) {
                            p_encounted = true;
                        } else {
                            z = z.Next;
                        }

                    }

                    u = w;
                    size = size - size_prime;

                } else {
                    /// STEP 5: ///
                    boolean q_encounted = false;
                    PointWrapper z = LS_u_i.getLast();
                    while (!q_encounted) {
                        // 5.1
                        for (PointWrapper pw : z.CrossPointers.values()) {
                            pw.Copy.Node = w;
                        }

                        z.Copy.Node = w;

                        // 5.2
                        for (int di = 0; di < z.CrossPointers.size(); di++) {
                            if (di == i)
                                continue;
                            PointWrapper crossPointer = z.CrossPointers.get(di);
                            Set<PointWrapper> LS_u_j = LS.get(di);
                            LS_u_j.delete(crossPointer);
                        }

                        // 5.3
                        LS_u_i.delete(z);

                        if (z.equals(q)) {
                            q_encounted = true;
                        } else {
                            q = q.Prev;
                        }
                    }

                    u = v;

                    size = size - size_prime;
                }
            }

            /// STEP 2: (size is now < n/2) ///
            for (int d = 0; d < dimension; d++) {
                Set<PointWrapper> LS_u_d = LS.get(d);
                for (PointWrapper pw : LS_u_d) {
                    pw.Copy.Node = u;
                }
            }

            /// STEP 6: ///
            List<PartialSplitTreeNode> leafs = getLeafs(root);

            Map<PartialSplitTreeNode, Map<Integer, Set<PointWrapper>>> LS_leafs = new HashMap<>();
            for (PartialSplitTreeNode leaf : leafs) {
                Map<Integer, Set<PointWrapper>> LS_leaf = new HashMap<>();
                for (int di = 0; di < dimension; di++) {
                    Set<PointWrapper> LS_leaf_d = LS_leaf.put(di, new Set<>());
                }
                LS_leafs.put(leaf, LS_leaf);
            }

            for (int d = 0; d < dimension; d++) {
                for (int pi = 0; pi < CLS.get(d).getSize(); pi++) {
                    PointWrapper pw = CLS.get(d).get(pi);
                    Map<Integer, Set<PointWrapper>> LS_leaf = LS_leafs.get(pw.Node);
                    Set<PointWrapper> LS_leaf_d = LS_leaf.get(d);
                    PointWrapper newPW = new PointWrapper(new Point(pw.getPoint().getCoords().clone()));
                    LS_leaf_d.insert(newPW);
                    int indexInsert = LS_leaf_d.getSize() - 1;

                    // TODO BUG WE CANNOT INSERT CROSS POINTERS LIKE THIS, BECAUSE WE HAVE TO ADD ALL THE POINTS FIRST,
                    // TODO BECAUSE THEY COME IS DIFFERENT RÆKKEFØLGE - since they are sorted different.
                    for (int di = 0; di < d; di++) {
                        System.out.println("DEBUG: " + d + " ind: " + indexInsert + " Size: " + LS_leaf.get(di).getSize());
                        PointWrapper other = LS_leaf.get(di).get(indexInsert);
                        // Add cross pointer in both directions:
                        other.CrossPointers.put(d, pw);
                        pw.CrossPointers.put(di, other);
                    }
                }
            }

            for (Map<Integer, Set<PointWrapper>> LS_leaf : LS_leafs.values()) {
                for (int d = 0; d < dimension; d++) {
                    Set<PointWrapper> points_d = LS_leaf.get(d);

                    for (int pi = 0; pi < points_d.getSize(); pi++) {
                        if (pi > 0) {
                            points_d.get(pi).Prev = points_d.get(pi - 1);
                        }
                        if (pi < points_d.getSize() - 1) {
                            points_d.get(pi).Next = points_d.get(pi + 1);
                        }
                    }

                    LS_leaf.put(d, points_d);
                }
            }

            // finally for each leaf compute the bounding box
            for(int leaf_index = 0; leaf_index < leafs.size(); leaf_index++){
                PartialSplitTreeNode leaf = leafs.get(leaf_index);
                Map<Integer, Set<PointWrapper>> LS_leaf = LS_leafs.get(leaf);

                // Since the points are sorted, we only need the first and the last point in each dimension,
                // to compute the bounding box:
                Set<Point> pointsNeedForBB = new Set<>();
                for(int d = 0; d < dimension; d++){
                    pointsNeedForBB.insert(LS_leaf.get(d).getFirst().getPoint());
                    pointsNeedForBB.insert(LS_leaf.get(d).getLast().getPoint());
                }
                leaf.setBoundingBox(new BoundingBox(pointsNeedForBB));
            }
        }

        private List<PartialSplitTreeNode> getLeafs(PartialSplitTreeNode root) {
            PartialSplitTreeNode a = root;
            List<PartialSplitTreeNode> leafs = new ArrayList<>();

            List<PartialSplitTreeNode> layer = new ArrayList<>();
            layer.add(a);
            while(!layer.isEmpty()) {
                List<PartialSplitTreeNode> newLayer = new ArrayList<>();
                for (PartialSplitTreeNode node : layer) {
                    if (node.leftChild != null){
                        newLayer.add(node.leftChild);
                    }
                    if (node.rightChild != null){
                        newLayer.add(node.rightChild);
                    }

                    if (node.leftChild == null && node.rightChild == null){
                        leafs.add(node);
                    }
                }

                layer = newLayer;
            }

            return leafs;
        }

        private class PartialSplitTreeNode {
            private final BoundingBox rectangle;
            private PartialSplitTreeNode rightChild;
            private PartialSplitTreeNode leftChild;
            private BoundingBox boundingBox;

            public PartialSplitTreeNode(BoundingBox rectangle) {
                this.rectangle = rectangle;
            }

            public void setLeftChild(PartialSplitTreeNode leftChild) {
                this.leftChild = leftChild;
            }

            public void setRightChild(PartialSplitTreeNode rightChild) {
                this.rightChild = rightChild;
            }

            public void setBoundingBox(BoundingBox boundingBox) {
                this.boundingBox = boundingBox;
            }
        }
    }
}
