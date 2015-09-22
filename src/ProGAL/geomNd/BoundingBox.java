package ProGAL.geomNd;

import ProGAL.dataStructures.Pair;
import ProGAL.dataStructures.Set;

import java.util.*;

import static java.lang.System.arraycopy;

public class BoundingBox {

    private List<Point> cornersPoints = new ArrayList<>();

    public List<Point> getCornerPoints(){
        return cornersPoints;
    }

    private int dimension;

    public BoundingBox(List<Point> cornersPoints){
        this.cornersPoints = cornersPoints;
        this.dimension = cornersPoints.get(0).getDimensions();

        if (cornersPoints.size() != Math.pow(2,dimension)){
            throw new IllegalArgumentException("The number of corner points, doesn't fit with the dimension");
        }
    }

    public BoundingBox(Set<Point> points) {
        if(points.isEmpty()){
            throw new IllegalArgumentException(
                    "Bounding box for empty point set is not defined");
        }

        Point firstPoint = points.get(0);
        this.dimension = firstPoint.getDimensions();

        // Test that all points have the same dimensions.
        for(int i = 1; i < points.getSize(); i++){
            Point p = points.get(i);
            if (p.getDimensions() != dimension){
                throw new IllegalArgumentException("All points must be in the same dimension");
            }
        }

        // Map each dimension to the lower and upper bound for this dimension.
        Map<Integer, Pair<Double, Double>> bounds = new HashMap<>();

        // Set init bound values, with the first point
        for(int i = 0; i < dimension; i++)
        {
            bounds.put(i, new Pair<>(firstPoint.getCoord(i), firstPoint.getCoord(i)));
        }

        // Iterate through the rest of the points, and change the bounds, when a coordinate exceeds the existing bound
        for(int pIndex = 1; pIndex < points.getSize(); pIndex++){
            Point point = points.get(pIndex);
            for(int dIndex = 0; dIndex < dimension; dIndex++){
                double coordinate = point.getCoord(dIndex);
                double lower = bounds.get(dIndex).fst;
                double upper = bounds.get(dIndex).snd;
                if (coordinate < lower) {
                    bounds.put(dIndex, new Pair<>(coordinate, upper));
                } else if (coordinate > upper) {
                    bounds.put(dIndex, new Pair<>(lower, coordinate));
                }
            }
        }

        for (int dIndex = 0; dIndex < dimension; dIndex++){
            double lowerBound = bounds.get(dIndex).fst;
            double upperBound = bounds.get(dIndex).snd;

            if (cornersPoints.size() == 0) {
                Point fstPoint = new Point(new double[dimension]);
                fstPoint.setCoord(dIndex, lowerBound);
                cornersPoints.add(fstPoint);
                Point sndPoint = new Point(new double[dimension]);
                sndPoint.setCoord(dIndex, upperBound);
                cornersPoints.add(sndPoint);
            } else {
                List<Point> pointsToAdd = new ArrayList<>();

                for(Point cornerPoint : cornersPoints)
                {
                    // each points is splitted into two for each iteration
                    double[] newCoords = new double[dimension];
                    arraycopy(cornerPoint.getCoords(),0, newCoords, 0, dimension);
                    Point newPoint = new Point(newCoords);
                    newPoint.setCoord(dIndex, lowerBound);
                    cornerPoint.setCoord(dIndex, upperBound);
                    pointsToAdd.add(newPoint);
                }

                cornersPoints.addAll(pointsToAdd);
            }
        }
    }

    public int getDimensionWithMaxLength(){
        double maxLength = -1;
        int maxDim = -1;
        for(int i = 0; i < dimension; i++){
            Point p1 = cornersPoints.get(0);
            for(int j = 1; j < cornersPoints.size(); j++) {
                Point p2 = cornersPoints.get(j);
                double diff = Math.abs(p1.getCoord(i) - p2.getCoord(i));
                if (diff > maxLength) {
                    maxLength = diff;
                    maxDim = i;
                }
            }
        }
        return maxDim;
    }

    public Pair<BoundingBox, BoundingBox> split(int splitDimension) {
        // Getting the "middle" of the box in the split dimension
        Point p1 = cornersPoints.get(0);
        double middle = -1;
        for(int i = 1; i < cornersPoints.size(); i++){
            Point p2 = cornersPoints.get(i);
            // We only need one pair of points, which differ in the splitting dimension
            if (p1.getCoord(splitDimension) != p2.getCoord(splitDimension)){
                middle = Math.abs(p1.getCoord(splitDimension) - p2.getCoord(splitDimension)) / 2;
                break;
            }
        }

        List<Point> pointsForBB1 = new ArrayList<>();
        List<Point> pointsForBB2 = new ArrayList<>();

        for(Point p : cornersPoints){
            if (p.getCoord(splitDimension) < middle){
                pointsForBB1.add(p.clone());
                Point np = p.clone();
                np.setCoord(splitDimension, middle);
                pointsForBB2.add(np);
            } else {
                pointsForBB2.add(p.clone());
                Point np = p.clone();
                np.setCoord(splitDimension, middle);
                pointsForBB1.add(np);
            }
        }

        System.out.println(middle);

        return new Pair<>(new BoundingBox(pointsForBB1), new BoundingBox(pointsForBB2));
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        for(Point point : cornersPoints)
        {
            builder.append(point.toString());
            builder.append('\n');
        }
        return builder.toString();
    }

}
