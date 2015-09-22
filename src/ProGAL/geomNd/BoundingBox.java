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

    public BoundingBox(Set<Point> points) {
        if(points.isEmpty()){
            throw new IllegalArgumentException(
                    "Bounding box for empty point set is not defined");
        }

        Point firstPoint = points.get(0);
        int dimension = firstPoint.getDimensions();

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
