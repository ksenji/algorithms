package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FastCollinearPoints {

  private final List<LineSegment> segments;

  public FastCollinearPoints(Point[] points) {
    if (points == null) {
      throw new IllegalArgumentException();
    }
    List<Line> lines = new ArrayList<>();
    Point[] copy = new Point[points.length];
    System.arraycopy(points, 0, copy, 0, points.length);

    for (int i = 0; i < points.length; i++) {
      Point pi = points[i];
      if (pi == null) {
        throw new IllegalArgumentException();
      }
      Arrays.sort(copy, (p1, p2) -> {
        if (p1 == null || p2 == null) {
          throw new IllegalArgumentException();
        }
        return pi.slopeOrder().compare(p1, p2);
      });

      int count = 1;
      double slope = pi.slopeTo(copy[0]);
      Point first = firstOf(pi, copy[0]);
      Point last = lastOf(pi, copy[0]);

      for (int j = 1; j < copy.length; j++) {
        double slope1 = pi.slopeTo(copy[j]);
        if (slope != slope1) {
          if (count >= 3) {
            lines.add(new Line(first, last));
          }
          count = 1;
          slope = slope1;
          first = firstOf(pi, copy[j]);
          last = lastOf(pi, copy[j]);
        } else {
          if (copy[j].compareTo(copy[j - 1]) == 0) {
            throw new IllegalArgumentException();
          }
          count++;
          first = firstOf(first, copy[j]);
          last = lastOf(last, copy[j]);

          if (j == points.length - 1 && count >= 3) {
            lines.add(new Line(first, last));
          }
        }
      }
    }
    this.segments = new ArrayList<>(lines.size());
    if (!lines.isEmpty()) {
      Collections.sort(lines, (a, b) -> {
        int cmp = a.p1.compareTo(b.p1);
        return cmp != 0 ? cmp : a.p2.compareTo(b.p2);
      });
      Iterator<Line> iter = lines.iterator();
      Line first = iter.next();
      this.segments.add(first.toLineSegment());
      while (iter.hasNext()) {
        Line curr = iter.next();
        if (!(first.p1.compareTo(curr.p1) == 0 && first.p2.compareTo(curr.p2) == 0)) {
          first = curr;
          this.segments.add(curr.toLineSegment());
        }
      }
    }
  }

  private static final class Line {
    private final Point p1;

    private final Point p2;

    Line(Point p1, Point p2) {
      this.p1 = p1;
      this.p2 = p2;
    }

    LineSegment toLineSegment() {
      return new LineSegment(p1, p2);
    }
  }

  private Point firstOf(Point p1, Point p2) {
    return p1.compareTo(p2) < 0 ? p1 : p2;
  }

  private Point lastOf(Point p1, Point p2) {
    return p1.compareTo(p2) > 0 ? p1 : p2;
  }

  public int numberOfSegments() {
    return segments.size();
  }

  public LineSegment[] segments() {
    return segments.toArray(new LineSegment[segments.size()]);
  }
}
