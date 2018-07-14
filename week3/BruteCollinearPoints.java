package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
  private final List<LineSegment> segments;

  public BruteCollinearPoints(Point[] points) {
    if (points == null) {
      throw new IllegalArgumentException();
    }
    Point[] copy = new Point[points.length];
    System.arraycopy(points, 0, copy, 0, copy.length);
    Arrays.sort(copy, (p1, p2) -> {
      if (p1 == null || p2 == null) {
        throw new IllegalArgumentException();
      }
      int cmp = p1.compareTo(p2);
      if (cmp == 0) {
        throw new IllegalArgumentException();
      }
      return cmp;
    });

    segments = new ArrayList<>();
    // finds all line segments containing 4 points
    for (int i = 0; i < copy.length; i++) {
      Point pi = copy[i];
      if (pi == null) {
        throw new IllegalArgumentException();
      }
      for (int j = i + 1; j < copy.length; j++) {
        Point pj = copy[j];
        for (int k = j + 1; k < copy.length; k++) {
          Point pk = copy[k];
          for (int m = k + 1; m < copy.length; m++) {
            Point pl = copy[m];
            double s1 = pi.slopeTo(pj);
            if (s1 == pi.slopeTo(pk) && s1 == pi.slopeTo(pl)) {
              segments.add(new LineSegment(pi, pl));
            }
          }
        }
      }
    }
  }

  public int numberOfSegments() {
    return segments.size();
  }

  public LineSegment[] segments() {
    return segments.toArray(new LineSegment[segments.size()]);
  }
}
