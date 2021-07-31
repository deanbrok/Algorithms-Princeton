import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {

    private final List<LineSegment> segments = new ArrayList<>();

    public BruteCollinearPoints(Point[] points)   {
        if (points == null) throw new IllegalArgumentException();

        checkNull(points);
        Point[] sortedPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(sortedPoints);
        checkDuplicate(sortedPoints);


        for (int i = 0; i < sortedPoints.length; i++) {
            for (int j = i + 1; j < sortedPoints.length; j++) {
                for (int k = j + 1; k < sortedPoints.length; k++) {
                    for (int m = k + 1; m < sortedPoints.length; m++) {

                        if (sortedPoints[i].slopeTo(sortedPoints[j]) == sortedPoints[i].slopeTo(sortedPoints[k])) {
                            if (sortedPoints[i].slopeTo(sortedPoints[k]) == sortedPoints[i].slopeTo(sortedPoints[m])) {
                                LineSegment currentSeg = new LineSegment(sortedPoints[i], sortedPoints[m]);
                                segments.add(currentSeg);
                          }
                        }

                    }
                }
            }
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

        Point p1 = new Point(1, 0);
        Point p2 = new Point(2, 0);
        Point p3 = new Point(3, 0);
        Point p4 = new Point(4, 0);
        Point p5 = new Point(5, 0);
        Point p6 = new Point(6, 0);
        Point p7 = new Point(7, 0);

        Point[] points = {p1, p2, p3, p4, p5, p6, p7};
        BruteCollinearPoints collinearPoints = new BruteCollinearPoints(points);

        for (LineSegment i: collinearPoints.segments()) {
            System.out.println(i);
        }

    }
}
