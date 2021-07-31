import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class FastCollinearPoints {

    private static final int MIN_CUTOFF = 4;
    private final List<LineSegment> segments = new ArrayList<>();

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException();

        checkNull(points);
        Point[] sortedPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(sortedPoints);
        checkDuplicate(sortedPoints);

        // Go through and sort by the slope of each point
        for (int i = 0; i < sortedPoints.length; i++) {
            Point[] sortBySlope = Arrays.copyOf(sortedPoints, sortedPoints.length);

            Point currentPoint = sortedPoints[i];

            Comparator<Point> slopeOrder = currentPoint.slopeOrder();
            Arrays.sort(sortBySlope, slopeOrder);

            int firstIndex = 1;
            for (int j = 2; j < sortBySlope.length; j++) {
                if (slopeOrder.compare(sortBySlope[firstIndex], sortBySlope[j]) == 0) {
                    continue;
                } else {
                    addToSegments(firstIndex, j, currentPoint, sortBySlope);
                    firstIndex = j;
                }
            }
            addToSegments(firstIndex, sortBySlope.length, currentPoint, sortBySlope);
        }
    }

    private void addToSegments(int firstIndex, int j, Point currentPoint, Point[] sortBySlope) {
        if (j - firstIndex + 1 >= MIN_CUTOFF && currentPoint.compareTo(sortBySlope[firstIndex]) < 0) {
            segments.add(new LineSegment(currentPoint, sortBySlope[j - 1]));
        }
    }

    private void checkNull(Point[] points) {
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException();
        }
    }

    private void checkDuplicate(Point[] sortedPoints) {
        for (int i = 0; i < sortedPoints.length - 1; i++) {
            if (sortedPoints[i].compareTo(sortedPoints[i + 1]) == 0) throw new IllegalArgumentException();
        }
    }

    public int numberOfSegments() {
        return segments.size();
    }
    public LineSegment[] segments()  {
        return segments.toArray(new LineSegment[0]);
    }

    public static void main(String[] args) {
//        Point p1 = new Point(1, 1);
//        Point p2 = new Point(1, 5);
//        Point p3 = new Point(3, 3);
//        Point p4 = new Point(5, 1);
//        Point p5 = new Point(5, 5);
//        Point p6 = new Point(10, 10);
//        Point p7 = new Point(100, 100);

        Point p8 = new Point(1000, 17000);
        Point p9 = new Point(1000, 27000);
        Point p10 = new Point(1000, 28000);
        Point p11 = new Point(1000, 31000);

        Point[] points = {p8, p9, p10, p11};

        FastCollinearPoints collinearPoints = new FastCollinearPoints(points);

        for (LineSegment i: collinearPoints.segments()) {
            System.out.println(i);
        }
    }

}
